package com.example.docarc.gui;

import com.example.docarc.bll.ProfileService;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.css.converter.StringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateProfileController implements Initializable {

    @FXML private CheckBox grayscaleCheckbox;
    @FXML private Slider contrastSlider;
    @FXML private Slider brightnessSlider;
    @FXML private TextField nameField;

    @FXML private Label contrastLabel;
    @FXML private Label brightnessLabel;
    @FXML private Label errorLabel;

    private ProfileService profileService;
    private AdminController adminController;


    public CreateProfileController(){
        profileService = new ProfileService();
    }

    public void setMainController(AdminController adminController){
        this.adminController = adminController;
    }
    @FXML
    private void onCancel(){
        Stage st = (Stage) grayscaleCheckbox.getScene().getWindow();
        st.close();
    }

    @FXML
    private void onSave(){
        boolean grayscale = grayscaleCheckbox.isSelected();
        double contrast = contrastSlider.valueProperty().get();
        double brightness = brightnessSlider.valueProperty().get();
        String name = nameField.getText();
        Task<Void> create_profile_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                profileService.createProfile(name, contrast, brightness, grayscale);
                return null;
            }
        };
        create_profile_task.setOnSucceeded(event -> {
            onCancel();
            this.adminController.displayProfiles();
        });
        create_profile_task.setOnFailed(event -> {
            Throwable exception = create_profile_task.getException();
            this.errorLabel.setText(exception.getMessage());
            this.errorLabel.setOpacity(1);
        });
        new Thread(create_profile_task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setStyle("-fx-text-fill: red");
        contrastLabel.textProperty().bindBidirectional(contrastSlider.valueProperty(), new NumberStringConverter("###.#"));
        brightnessLabel.textProperty().bindBidirectional(brightnessSlider.valueProperty(), new NumberStringConverter("###.#"));
        this.errorLabel.setOpacity(0);
    }
}
