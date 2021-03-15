package dev.el_nico.dam2_psp_p4.gui;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.el_nico.dam2_psp_p4.modelo.SesionFtp;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static final ExecutorService worker = Executors.newSingleThreadExecutor();

    @Override
    public void start(Stage stage) throws IOException {
        
        stage.setResizable(false);
        Gui.cargarEscena("login", stage);

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        worker.shutdown();
        SesionFtp.logout();
    }
}