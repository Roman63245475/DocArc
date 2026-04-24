package com.example.docarc.gui;

import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.bll.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEditUserController implements Initializable {

    private AdminController adminController;
    private ParentUser parentUser;
    private AuthService authService;

    @FXML private TextField userNameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<Role> userRoleBox;
    @FXML private Label errorLabel;
    @FXML private Region eyeIcon;
    @FXML private TextField revealField;


    public AddEditUserController(){
        this.authService = new AuthService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setOpacity(0.0);
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.userRoleBox.getItems().addAll(Role.values());
        revealField.textProperty().bindBidirectional(passwordField.textProperty());
    }



    @FXML
    private void createUser(){
        String username = userNameField.getText();
        String password = passwordField.getText();
        Role role = userRoleBox.getSelectionModel().getSelectedItem();
        if (username.isEmpty() || password.isEmpty() || role == null){
            this.errorLabel.setText("Please fill all the fields");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        Task<Void> createUserTask = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                authService.createUser(username, password, role);
                return null;
            }
        };

        createUserTask.setOnSucceeded(event -> {
            Stage stage = (Stage) this.errorLabel.getScene().getWindow();
            stage.close();
            this.adminController.refreshUserTable();
        });
        createUserTask.setOnFailed(event -> {
            Throwable cause = createUserTask.getException();
            this.errorLabel.setText(cause.getMessage());
            this.errorLabel.setOpacity(1.0);
        });
        new Thread(createUserTask).start();
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

    public void setUser(ParentUser usr){
        this.parentUser = usr;
    }

    public void setController(AdminController adminController) {
        this.adminController = adminController;
    }


}
