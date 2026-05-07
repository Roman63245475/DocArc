package com.example.docarc.gui;

import com.example.docarc.be.User;
import com.example.docarc.bll.DataService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateBoxController implements Initializable {

    @FXML private TextField boxName;
    @FXML private Label errorLabel;
    private DataService boxService;
    private User responsibleUser;

    public CreateBoxController() {
        boxService = new DataService();
    }

    public void setUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) boxName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSave() {
        String name = boxName.getText();
        Task<Void> createBoxTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boxService.createBox(name, responsibleUser);
                return null;
            }
        };

        createBoxTask.setOnSucceeded(e -> {
            onCancel();
        });

        createBoxTask.setOnFailed(e -> {
            Throwable exception = createBoxTask.getException();
            errorLabel.setText(exception.getMessage());
            errorLabel.setOpacity(1.0);
        });
        new Thread(createBoxTask).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setStyle("-fx-text-fill: red");
    }
}
