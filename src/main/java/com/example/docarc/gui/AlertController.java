package com.example.docarc.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertController {

    @FXML private Label headerLabel;
    @FXML private Label descLabel;

    private boolean confirmed = false;

    public void setText(String headerText, String descText){
        headerLabel.setText(headerText);
        descLabel.setText(descText);
    }

    @FXML private void onConfirm() {
        confirmed = true;
        Stage stage = (Stage) headerLabel.getScene().getWindow();
        stage.close();
    }

    @FXML private void onCancel() {
        Stage stage = (Stage) headerLabel.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
