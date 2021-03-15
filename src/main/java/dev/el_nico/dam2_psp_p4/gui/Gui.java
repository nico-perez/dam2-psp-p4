package dev.el_nico.dam2_psp_p4.gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Gui {

    public static final Image ICONO_USUARIO = cargarIcono("user");
    public static final Image ICONO_CARPETA = cargarIcono("folder");
    public static final Image ICONO_CARPETA_VACIA = cargarIcono("emptyfolder");
    public static final Image ICONO_ARCHIVO = cargarIcono("file");

    public static Controlador cargarEscena(String fxml, Stage ventana) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        Scene escenaNueva = new Scene(fxmlLoader.load());

        Controlador c = fxmlLoader.getController();
        c.setStage(ventana);
        c.setScene(escenaNueva);

        ventana.setScene(escenaNueva);
        ventana.show();

        return c;
    }
    
    private static synchronized Image cargarIcono(String nombre) {
        return new Image(App.class.getResourceAsStream("icon/" + nombre + ".png"));
    }
}
