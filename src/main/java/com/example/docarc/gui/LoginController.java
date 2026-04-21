package com.example.docarc.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private PasswordField passwordField;
    @FXML private Region eyeIcon;
    @FXML private TextField revealField;







    @Override
    public void initialize(URL location, ResourceBundle resources) {
        revealField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void loginClick(){
        System.out.println("login");
    }

    @FXML
    private void toggleVisibility() {
        if (passwordField.isVisible()) {
            eyeIcon.setId("closed-eye-icon");
            passwordField.setVisible(false);
            revealField.setVisible(true);
        }else{
            eyeIcon.setId("open-eye-icon");
            passwordField.setVisible(true);
            revealField.setVisible(false);
        }
    }


}
