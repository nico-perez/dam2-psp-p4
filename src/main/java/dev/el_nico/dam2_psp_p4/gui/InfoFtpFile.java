package dev.el_nico.dam2_psp_p4.gui;

import java.text.SimpleDateFormat;

import org.apache.commons.net.ftp.FTPFile;

import dev.el_nico.dam2_psp_p4.modelo.SesionFtp;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class InfoFtpFile {
    
    public static final Callback<TreeView<InfoFtpFile>, TreeCell<InfoFtpFile>> CELL_FACTORY = v -> {

        return new TreeCell<>() {

            @Override
            protected void updateItem(InfoFtpFile item, boolean empty) {

                super.updateItem(item, empty);

                if (item != null) {

                    if (item.esRaiz) {
                        setGraphic(new ImageView(Gui.ICONO_USUARIO));
                    } else if (item.esArchivo) {
                        setGraphic(new ImageView(Gui.ICONO_ARCHIVO));
                    } else if (item.esDirectorio && item.estaVacio) {
                        setGraphic(new ImageView(Gui.ICONO_CARPETA_VACIA));
                    } else {
                        setGraphic(new ImageView(Gui.ICONO_CARPETA));
                    }
                 
                } else {

                    setGraphic(null);

                }

                setText(empty ? null : item != null ? item.getNombre() : "ERROR???");
            }

        };
    };

    private final FTPFile file;

    private String ruta;
    private boolean esArchivo;
    private boolean esDirectorio;
    private boolean estaVacio;
    private boolean esRaiz;

    private InfoFtpFile() { 
        file = null;
        ruta = "";
        esArchivo = esDirectorio = estaVacio = esRaiz = false;
    }

    public InfoFtpFile(FTPFile file, String ruta) {
        this.file = file;
        this.ruta = ruta;
        esArchivo = file.isFile();
        esDirectorio = file.isDirectory();
    }

    public String getNombre() {
        return esRaiz ? ruta : file.getName();
    }

    public static InfoFtpFile root(String user) {
        InfoFtpFile file = new InfoFtpFile();
        file.esRaiz = true;
        file.ruta = "Directorio de " + user;
        return file;
    }

    public void setVacio(boolean estaVacio) {
        this.estaVacio = estaVacio;
    }

    public FTPFile getFile() {
        return file;
    }

    public String getTipo() {
        return esArchivo ? "Archivo" : esDirectorio ? "Directorio" : "????";
    }

    public boolean esRaiz() {
        return esRaiz;
    }

    public String getPermisos() {
        String p = "";

        if (file != null) {
            p += file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) ? "r" : "-";
            p += file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ? "w" : "-";
            p += file.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION) ? "x" : "-";

            p += file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION) ? "r" : "-";
            p += file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION) ? "w" : "-";
            p += file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.EXECUTE_PERMISSION) ? "x" : "-";

            p += file.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.READ_PERMISSION) ? "r" : "-";
            p += file.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.WRITE_PERMISSION) ? "w" : "-";
            p += file.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.EXECUTE_PERMISSION) ? "x" : "-";
        }

        return p;
    }

    public String getTamanio() {
        return "" + (file != null ? file.getSize() : 0);
    }

    public String getGrupo() {
        return file != null ? file.getGroup() : "";
    }

    public String getLink() {
        return file != null ? file.getLink() : "";
    }

    public String getUsuario() {
        return file != null ? file.getUser() : SesionFtp.getUser();
    }

    public String getTimestamp() {
        if (file != null) {
            return new SimpleDateFormat("dd MMM yyyy, hh:mm:ss").format(file.getTimestamp().getTime());
        } else {
            return "";
        }
    }

    public String getRuta() {
        return ruta;
    }

    public boolean esDirectorio() {
        return file.isDirectory();
    }


}
