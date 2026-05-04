package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.User;
import com.example.docarc.bll.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private PasswordField passwordField;
    @FXML private Region eyeIcon;
    @FXML private TextField revealField;
    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private Button loginButton;

    private AuthService authService;

    public LoginController(){
        this.authService = new AuthService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.errorLabel.setOpacity(0.0);
        revealField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void loginClick(){
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();
        if (password.isEmpty() || username.isEmpty()){
            this.errorLabel.setText("Both username and password fields need to be filled");
            this.errorLabel.setOpacity(1.0);
            return;
        }
        loginButton.setDisable(true);
        Task<ParentUser> loginTask = new Task<ParentUser>(){
            @Override
            protected ParentUser call() throws Exception {
                return authService.login(username, password);
            }
        };
        loginTask.setOnSucceeded(event -> {
            ParentUser user = loginTask.getValue();
            try {
                String fileName = (user instanceof Admin) ? "admin_view.fxml" : "template_uploading.fxml";
                String title = (user instanceof Admin) ? "Admin panel" : "User panel";
                Object obj = UIHelper.openNewWindow(fileName, title, false);
                if (user instanceof User){
                    ((UploadTemplateCont) obj).setUser((User) user);//changed
                }
                else{
                    ((AdminController) obj).setUser((Admin) user);
                }
                Stage currentStage = (Stage) this.usernameField.getScene().getWindow();
                currentStage.close();
            }
            catch (IOException e) {
                loginButton.setDisable(false);
                this.errorLabel.setText("Page can't be rendered");
                this.errorLabel.setOpacity(1.0);
                return;
                //or here needs to be an alert
            }
        });

        loginTask.setOnFailed(event -> {
            Throwable cause = loginTask.getException();
            loginButton.setDisable(false);
            System.out.println(cause.getMessage()
            );
            this.errorLabel.setText(cause.getMessage());
            this.errorLabel.setOpacity(1.0);
        });
        new Thread(loginTask).start();
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
