package dev.el_nico.dam2_psp_p4.gui;

import dev.el_nico.dam2_psp_p4.modelo.SesionFtp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class Controlador {
    
    private @FXML AnchorPane guiLogin;
    private @FXML HBox guiFilezilla;

    private @FXML TextField user;
    private @FXML PasswordField pass;
    private @FXML Button btnLogin;
    private @FXML ImageView gifCargando;

    public void onClickBtnLogin() {
        String stringUser = user.getText();
        String stringPass = pass.getText();

        if (stringUser.equals("") || stringPass.equals("")) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText("No se ha podido iniciar sesión.");
            alert.setContentText("No se han rellenado los campos necesarios");

            alert.showAndWait();

        } else {

            gifCargando.setVisible(true);
            btnLogin.setText("");

            App.worker.execute(() -> {

                if (SesionFtp.login(stringUser, stringPass)) {

                    Platform.runLater(() -> {
                        guiFilezilla.setVisible(true);
                        guiLogin.setDisable(true);
                        guiLogin.setVisible(false);
                    });
                    
                } else {

                    Platform.runLater(() -> {
                        gifCargando.setVisible(false);
                        btnLogin.setText("Dale");
                    });
                    
                }
            });
            
        }

        

    }

}
