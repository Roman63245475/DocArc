package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.Client;
import com.example.docarc.bll.ClientService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class CreateClientController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextField cityField;

    @FXML private Label titleLabel;
    @FXML private Label errorLabel;

    @FXML private Button confirmButton;


    private ClientService clientService;

    private Admin adminUser;
    private AdminController adminController;

    private Client client;

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

    public void setClient(Client client){
        this.client = client;
        this.nameField.setText(client.getName());
        this.countryField.setText(client.getCountry());
        this.cityField.setText(client.getCity());
        confirmButton.setOnAction(event -> {
            onEdit();
        });
        titleLabel.setText("Client Editing");
    }

    @FXML
    private void onSave(ActionEvent event) {
        Button button = (Button) event.getSource();
        String name = this.nameField.getText();
        String country = this.countryField.getText();
        String city = this.cityField.getText();
        Task<Void> create_client_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                clientService.createClient(name, country, city, adminUser);
                return null;
            }
        };

        button.disableProperty().bind(create_client_task.runningProperty());

        create_client_task.setOnSucceeded(e -> {
            onCancel();
            this.adminController.displayClients();
        });
        create_client_task.setOnFailed(e -> {
            Throwable exception = create_client_task.getException();
            errorLabel.setText(exception.getMessage());
            errorLabel.setOpacity(1);
        });
        new Thread(create_client_task).start();
    }

    private void onEdit(){
        String name = this.nameField.getText();
        String country = this.countryField.getText();
        String city = this.cityField.getText();

        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                clientService.updateClient(client, name, country, city);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            adminController.displayClients();
            onCancel();
        });

        task.setOnFailed(e -> {
            errorLabel.setText(task.getException().getMessage());
            errorLabel.setOpacity(1.0);
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setStyle("-fx-text-fill: red");
        errorLabel.setOpacity(0.0);
    }
}
