package dev.el_nico.dam2_psp_p4.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPFile;

import dev.el_nico.dam2_psp_p4.modelo.SesionFtp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControladorPrincipal extends Controlador implements Initializable {
    
    private @FXML Button botonAniadirArchivo;
    private @FXML Button botonEliminarArchivo;
    private @FXML Button botonAniadirCarpeta;
    private @FXML Button botonEliminarCarpeta;
    private @FXML Button botonCerrarSesion;
    private @FXML Button botonDescargarArchivo;

    private @FXML HBox migasDePan;
    private @FXML ScrollPane scrollMigas;
    private @FXML TreeView<InfoFtpFile> arbolArchivos;
    private @FXML ListView<String> listaDetalles;
    private @FXML TextArea contenido;

    private @FXML Label etiquetaUrlConexion;
    private @FXML Label etiquetaUsuarioConectado;

    private TreeItem<InfoFtpFile> root;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL arg0, ResourceBundle arg1) {

        etiquetaUsuarioConectado.setText(SesionFtp.getUser());

        arbolArchivos.setCellFactory(InfoFtpFile.CELL_FACTORY);
        arbolArchivos.setOnMouseClicked(e -> {
        
            if (e.getTarget() instanceof Node) {
                try {

                    InfoFtpFile info = ((TreeCell<InfoFtpFile>) e.getTarget()).getItem();

                    System.out.println("Nodo clicado: " + info.getRuta() + " - " + info.getNombre());

                    if (info.esRaiz()) {
                        botonEliminarCarpeta.setDisable(true);
                        botonEliminarArchivo.setDisable(true);
                        botonDescargarArchivo.setDisable(true);
                    } else {
                        if (info.esDirectorio()) {
                            botonEliminarCarpeta.setDisable(false);
                            botonEliminarArchivo.setDisable(true);
                            botonDescargarArchivo.setDisable(true);

                        } else {
                            botonEliminarCarpeta.setDisable(true);
                            botonEliminarArchivo.setDisable(false);
                            botonDescargarArchivo.setDisable(false);

                        }
                    }

                    if (e.getClickCount() == 2) {

                        if (info.esDirectorio()) {

                            TreeItem<InfoFtpFile> este = ((TreeCell<InfoFtpFile>) e.getTarget()).getTreeItem();
                            este.setExpanded(true);
                            arbolArchivos.setRoot(este);

                            migasDePan.getChildren().clear();

                            while (este != null)
                            {
                                LinkCarpeta nuevo = new LinkCarpeta("/" + este.getValue().getNombre(), este);
                                nuevo.setOnAction(o -> {
                                    arbolArchivos.setRoot(nuevo.getItem());
                                    int i = migasDePan.getChildren().indexOf(nuevo);
                                    try {
                                        migasDePan.getChildren().remove(i + 1, migasDePan.getChildren().size());
                                    } catch (IndexOutOfBoundsException edersrtg) {
                                        edersrtg.printStackTrace();
                                    }
                                });
                                
                                migasDePan.getChildren().add(0, nuevo);
                                
                                este = este.getParent();
                            }
                            scrollMigas.setHvalue(scrollMigas.getHmax());
                        }                        

                    } else {

                        FTPFile f = info.getFile();
                        if (f != null && f.isFile()) {
                            App.worker.execute(() -> {
    
                                String ruta = info.getRuta() + "/" + info.getNombre();
    
                                String s = SesionFtp.descargarVistaPrevia(ruta);
                                Platform.runLater(() -> {
                                   contenido.setText(s); 
                                });
                            });
                        } else {
                            contenido.setText("");
                        }
    
                        ObservableList<String> atributos = FXCollections.observableArrayList(
                            "NOMBRE: " + info.getNombre(),
                            "TAMAÑO: " + info.getTamanio(),
                            "GRUPO: " + info.getGrupo(),
                            "ENLACE: " + info.getLink(),
                            "TIMESTAMP: " + info.getTimestamp(),
                            "USUARIO: " + info.getUsuario(),
                            "TIPO: " + info.getTipo(),
                            "PERMISOS: " + info.getPermisos()
                        );
    
                        listaDetalles.getItems().setAll(atributos);
                    }

                   
                } catch (ClassCastException | NullPointerException ex) {
                    /* nada */
                }
            }
        });

        App.worker.execute(() -> {
            
            root = new TreeItem<>(InfoFtpFile.root(SesionFtp.getUser()));
            actualizarArbolDeArchivos("", root);

            Platform.runLater(() -> {
                
                arbolArchivos.setRoot(root);
                migasDePan.getChildren().add(new LinkCarpeta("/Directorio de " + SesionFtp.getUser(), root));
                
            });

        });

    }

    private void actualizarArbolDeArchivos(String ruta, TreeItem<InfoFtpFile> treeItem) {

        treeItem.setExpanded(true);

        ObservableList<FTPFile> archivos = FXCollections.observableArrayList(SesionFtp.listarArchivos(ruta));

        archivos.stream().map(v -> new InfoFtpFile(v, ruta)).map(TreeItem::new).forEach(treeItem.getChildren()::add);

        ObservableList<FTPFile> directorios = FXCollections.observableArrayList(SesionFtp.listarDirectorios(ruta));
        
        if (!directorios.isEmpty()) {
            directorios.stream().map(v -> new InfoFtpFile(v, ruta)).map(TreeItem::new).forEach(ti -> {
                treeItem.getChildren().add(ti); 
                actualizarArbolDeArchivos(ruta + "/" + ti.getValue().getNombre(), ti); 
            });
        } else if (archivos.isEmpty()) {
            // la carpeta no tiene archivos ni directorios
            treeItem.getValue().setVacio(true);
        }
    }

    public void cerrarSesion() {
        SesionFtp.logout();
        try {
            Stage login = new Stage();
            login.setResizable(false);
            Gui.cargarEscena("login", login);
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aniadirArchivo() throws IOException {

        TreeItem<InfoFtpFile> selecc = arbolArchivos.getSelectionModel().getSelectedItem();
        String ruta;
        if (selecc != null) {
            if (selecc.getValue().esRaiz()) {
                ruta = "/";
            } else if (selecc.getValue().esDirectorio()) {
                ruta = selecc.getValue().getRuta() + "/" + selecc.getValue().getNombre() + "/";
            } else {
                ruta = selecc.getValue().getRuta() + "/";
            }
        } else {
            ruta = "/";
        }

        FileChooser elegirArchivo = new FileChooser();
        elegirArchivo.setTitle("Elige un archivo");
        
        File file = elegirArchivo.showOpenDialog(stage);

        if (file.isFile()) {

            if (SesionFtp.subirFichero(ruta, file.getAbsolutePath(), true)) {

                

                if (selecc == null) {

                    FTPFile subido = SesionFtp.listarArchivos(ruta).stream().dropWhile(f -> !f.getName().equals(file.getName())).findAny().get();
                    root.getChildren().add(new TreeItem<>(new InfoFtpFile(subido, ruta)));

                } else if (selecc.getValue().esRaiz() ||  selecc.getValue().esDirectorio()) {

                    FTPFile subido = SesionFtp.listarArchivos(ruta).stream().dropWhile(f -> !f.getName().equals(file.getName())).findAny().get();
                    selecc.getChildren().add(new TreeItem<>(new InfoFtpFile(subido, ruta)));

                } else  {

                    FTPFile subido = SesionFtp.listarArchivos(selecc.getValue().getRuta()).stream().dropWhile(f -> !f.getName().equals(file.getName())).findAny().get();
                    selecc.getParent().getChildren().add(new TreeItem<>(new InfoFtpFile(subido, ruta)));

                } 
                
            }

        } else {
            System.out.println("El archivo elegido no se puede subir");
        }
    }

    public void descargarArchivo() throws IOException {

        TreeItem<InfoFtpFile> selecc = arbolArchivos.getSelectionModel().getSelectedItem();

        if (selecc == null || selecc.getValue() == null || selecc.getValue().esDirectorio() || selecc.getValue().esRaiz()) {
            return;
        }

        SesionFtp.descargarFichero(selecc.getValue().getRuta() + "/" + selecc.getValue().getNombre());
    }

    public void eliminarArchivo() throws IOException {

        TreeItem<InfoFtpFile> selecc = arbolArchivos.getSelectionModel().getSelectedItem();

        if (selecc == null || selecc.getValue() == null || selecc.getValue().esDirectorio()) {
            return;
        }

        String ruta = selecc.getValue().getRuta() + "/" + selecc.getValue().getNombre();        

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar acción");
        alert.setHeaderText("¿Eliminar el siguiente fichero?");
        alert.setContentText(ruta);

        alert.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK && SesionFtp.eliminarFichero(ruta)) { 
                selecc.getParent().getChildren().remove(selecc);
            }
        });

    }

    public void eliminarCarpeta() throws IOException {

        TreeItem<InfoFtpFile> selecc = arbolArchivos.getSelectionModel().getSelectedItem();

        if (selecc == null || selecc.getValue() == null || !selecc.getValue().esDirectorio()) {
            return;
        }

        String ruta = selecc.getValue().getRuta() + "/" + selecc.getValue().getNombre();        

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar acción");
        alert.setHeaderText("¿Eliminar el siguiente directorio?");
        alert.setContentText(ruta);

        alert.showAndWait().ifPresent(b -> { 
            if (b == ButtonType.OK && SesionFtp.eliminarDirectorio(ruta)) { 
                selecc.getParent().getChildren().remove(selecc);
            } 
        });
        
    }

    public void aniadirCarpeta() throws IOException {

        TreeItem<InfoFtpFile> selecc = arbolArchivos.getSelectionModel().getSelectedItem();
        String ruta;
        if (selecc != null) {
            if (selecc.getValue().esRaiz()) {
                ruta = "/";
            } else if (selecc.getValue().esDirectorio()) {
                ruta = selecc.getValue().getRuta() + "/" + selecc.getValue().getNombre() + "/";
            } else {
                ruta = selecc.getValue().getRuta() + "/";
            }
        } else {
            ruta = "/";
        }

        TextInputDialog tid = new TextInputDialog();
        tid.setHeaderText("Nombre para la nueva carpeta");
        tid.setContentText(ruta.length() < 30 ? ruta : "..." + ruta.substring(ruta.length() - 25));

        tid.showAndWait().ifPresent(n -> { 
            SesionFtp.crearDirectorio(ruta + n);
            SesionFtp.listarDirectorios(ruta).stream()
                    .filter(f -> f.getName().equals(n))
                    .findFirst()
                    .ifPresent(f -> {

                        TreeItem<InfoFtpFile> nodoCarpeta = new TreeItem<InfoFtpFile>(new InfoFtpFile(f, ruta));
                        nodoCarpeta.getValue().setVacio(true);

                        if (selecc == null || selecc.getValue().esRaiz()) {
                            root.getChildren().add(nodoCarpeta);
                        } else {
                            if (selecc.getValue().esDirectorio()) {
                                selecc.getChildren().add(nodoCarpeta);
                                selecc.getValue().setVacio(false);
                            } else {
                                selecc.getParent().getChildren().add(nodoCarpeta);
                                selecc.getParent().getValue().setVacio(false);
                            }
                        }

                    });
            arbolArchivos.refresh();
        });
    }
}
