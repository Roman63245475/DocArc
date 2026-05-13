package com.example.docarc.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class CreateClientController {

    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextField cityField;

    @FXML
    private void onCancel(){
        Stage st = (Stage) nameField.getScene().getWindow();
        st.close();
    }

    @FXML
    private void onSave(){
        System.out.println("I'm invoked");
    }
}
