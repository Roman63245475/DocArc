package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.bll.ClientService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class CreateClientController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextField cityField;
    @FXML private Label errorLabel;
    private ClientService clientService;
    private Admin adminUser;
    private AdminController adminController;

    public CreateClientController(){
        this.clientService = new ClientService();
    }

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    @FXML
    private void onCancel(){
        Stage st = (Stage) nameField.getScene().getWindow();
        st.close();
    }

    public void setAdminUser(Admin admin){
        this.adminUser = admin;
    }

    @FXML
    private void onSave(){
        String name = this.nameField.getText();
        String country = this.countryField.getText();
        String city = this.cityField.getText();
        Task<Void> create_profile_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                clientService.createClient(name, country, city, adminUser);
                return null;
            }
        };
        create_profile_task.setOnSucceeded(e -> {
            onCancel();
            this.adminController.displayClients();
        });
        create_profile_task.setOnFailed(e -> {
            Throwable exception = create_profile_task.getException();
            errorLabel.setText(exception.getMessage());
            errorLabel.setOpacity(1);
        });
        new Thread(create_profile_task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.errorLabel.setOpacity(0);
    }
}
