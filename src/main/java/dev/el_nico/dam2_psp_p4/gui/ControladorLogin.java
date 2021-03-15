package dev.el_nico.dam2_psp_p4.gui;

import java.io.IOException;

import dev.el_nico.dam2_psp_p4.modelo.SesionFtp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ControladorLogin extends Controlador {
    
    private @FXML TextField user;
    private @FXML PasswordField pass;
    private @FXML Button btnLogin;
    private @FXML ImageView gifCargando;

    public void onClickBtnLogin() {
        String stringUser = user.getText();
        String stringPass = pass.getText();

        if (stringUser.equals("") || stringPass.equals("")) {

            Alert alert = new Alert(AlertType.ERROR);
            //alert.setTitle("Atención");
            alert.setHeaderText("No se ha podido iniciar sesión.");
            alert.setContentText("No se han rellenado los campos necesarios");

            alert.showAndWait();

        } else {

            gifCargando.setVisible(true);
            btnLogin.setText("");

            App.worker.execute(() -> {

                if (SesionFtp.login(stringUser, stringPass)) {

                    Platform.runLater(() -> {

                        stage.close();
                        try {
                            Stage ventanaPrincipal = new Stage();
                            ventanaPrincipal.setMinHeight(400);
                            ventanaPrincipal.setMinWidth(600);
                            Gui.cargarEscena("main", ventanaPrincipal);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                    
                } else {

                    Platform.runLater(() -> {

                        gifCargando.setVisible(false);
                        btnLogin.setText("Dale");

                        Alert alert = new Alert(AlertType.ERROR);
                        //alert.setTitle("Atención");
                        alert.setHeaderText("No se ha podido iniciar sesión.");
                        alert.setContentText(SesionFtp.getReplyString());

                        alert.showAndWait();

                    });
                    
                }
            });
            
        }

        

    }

}
