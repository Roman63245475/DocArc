package com.example.docarc.gui;

import com.example.docarc.be.Client;
import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.bll.ProfileService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AssignProfileToClientController implements Initializable {

    @FXML private Label profileNameLabel;
    @FXML private ListView<Client> eligibleClientsList;
    @FXML private Label errorLabel;
    @FXML private Button assignButton;

    private final ProfileService profileService = new ProfileService();
    private final ObservableList<Client> eligibleClients = FXCollections.observableArrayList();

    private Profile profile;
    private AdminController adminController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eligibleClientsList.setItems(eligibleClients);
        errorLabel.setStyle("-fx-text-fill: red");
        hideError();
        assignButton.disableProperty().bind(
                Bindings.or(
                        Bindings.isEmpty(eligibleClients),
                        eligibleClientsList.getSelectionModel().selectedItemProperty().isNull()));
    }

    public void setProfileAndAdmin(Profile profile, AdminController adminController) {
        this.profile = profile;
        this.adminController = adminController;
        profileNameLabel.setText(profile.getName());
        loadEligibleClients();
    }

    private void loadEligibleClients() {
        Task<List<Client>> task = new Task<>() {
            @Override
            protected List<Client> call() throws Exception {
                return profileService.getClientsEligibleForProfileAssignment(profile.getId());
            }
        };
        task.setOnSucceeded(e -> eligibleClients.setAll(task.getValue()));
        task.setOnFailed(e -> showError(task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void onAssign(ActionEvent event) {
        Button button = (Button) event.getSource();
        Client selectedClient = eligibleClientsList.getSelectionModel().getSelectedItem();
        if (selectedClient == null || profile == null) {
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                profileService.assignProfileToClient(profile.getId(), selectedClient.getId());
                return null;
            }
        };

        button.disableProperty().bind(task.runningProperty());

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            closeStage();
            if (adminController != null) {
                adminController.displayProfiles();
            }
        }));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String msg = ex != null ? ex.getMessage() : "Unknown error";
            Platform.runLater(() -> showError(msg));
        });
        new Thread(task).start();
    }

    @FXML
    private void onCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage st = (Stage) eligibleClientsList.getScene().getWindow();
        st.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
