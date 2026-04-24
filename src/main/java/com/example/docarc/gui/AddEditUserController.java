package com.example.docarc.gui;

import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.bll.AuthService;
import com.example.docarc.bll.UserService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEditUserController implements Initializable {

    private AdminController adminController;
    private ParentUser user;
    private AuthService authService;
    private UserService userService;

    @FXML private TextField userNameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<Role> userRoleBox;
    @FXML private Label errorLabel;
    @FXML private Region eyeIcon;
    @FXML private TextField revealField;
    @FXML private Button createUserButton;
    @FXML private Label titleLabelTop;
    @FXML private Label titleLabelBottom;


    public AddEditUserController(){
        this.authService = new AuthService();
        this.userService = new UserService();
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
        this.createUserButton.setDisable(true);
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
            this.createUserButton.setDisable(false);
        });
        new Thread(createUserTask).start();
    }

    @FXML
    private void editUser(){
        String username = userNameField.getText();
        String password = passwordField.getText();
        Role role = userRoleBox.getSelectionModel().getSelectedItem();
        if (username.isEmpty() || role == null){
            this.errorLabel.setText("Username and Role fields have to be filled out");
            return;
        }
        this.createUserButton.setDisable(true);
        Task<Void> editUserTask = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                userService.editUser(user, username, password, role);
                return null;
            }
        };
        editUserTask.setOnSucceeded(event -> {
            Stage stage = (Stage) this.errorLabel.getScene().getWindow();
            stage.close();
            this.adminController.refreshUserTable();
        });
        editUserTask.setOnFailed(event -> {
            Throwable cause = editUserTask.getException();
            cause.printStackTrace();
            this.errorLabel.setText(cause.getMessage());
            this.errorLabel.setOpacity(1.0);
            this.createUserButton.setDisable(false);
        });
        new Thread(editUserTask).start();


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
        this.user = usr;
        this.createUserButton.setText("Save");
        this.titleLabelTop.setText("Edit User");
        this.titleLabelBottom.setText("Edit already existing user");
        this.createUserButton.setOnAction(event -> {editUser();});
        fillFields();
    }

    private void fillFields(){
        this.userNameField.setText(this.user.getUsername());
        this.userRoleBox.setValue(this.user.getRole());
    }

    public void setController(AdminController adminController) {
        this.adminController = adminController;
    }


}
