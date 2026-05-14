package com.example.docarc.gui;

import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.bll.DataService;
import com.example.docarc.bll.ProfileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreateBoxController implements Initializable {

    @FXML private TextField boxName;
    @FXML private Label errorLabel;
    @FXML private ComboBox<Profile> profileComboBox;
    private DataService boxService;
    private ProfileService profileService;
    private User responsibleUser;
    private ObservableList<Profile> clientProfilesList = FXCollections.observableArrayList();

    public CreateBoxController() {
        boxService = new DataService();
        profileService = new ProfileService();
    }

    public void setUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
        getProfiles();
    }

    private void getProfiles(){
        Task<List<Profile>> get_profiles_task = new Task<List<Profile>>() {
            @Override
            protected List<Profile> call() throws Exception {
                return profileService.getProfilesByClient(responsibleUser.getClientId());
            }
        };
        get_profiles_task.setOnSucceeded(event -> {
            this.clientProfilesList.setAll(get_profiles_task.getValue());
        });
        get_profiles_task.setOnFailed(event -> {
            // maybe we can just be closing this window then
            System.out.println(get_profiles_task.getException().getMessage());
        });
        new Thread(get_profiles_task).start();
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) boxName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSave() {
        String name = boxName.getText();
        Profile profile = profileComboBox.getSelectionModel().getSelectedItem();
        Task<Void> createBoxTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boxService.createBox(name, profile, responsibleUser);
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
        this.errorLabel.setOpacity(0);
        this.profileComboBox.setItems(clientProfilesList);
    }
}
