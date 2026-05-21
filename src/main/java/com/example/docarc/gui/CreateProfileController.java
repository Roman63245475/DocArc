package com.example.docarc.gui;

import com.example.docarc.be.Profile;
import com.example.docarc.bll.ImageProcessor;
import com.example.docarc.bll.ProfileService;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.css.converter.StringConverter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateProfileController implements Initializable {

    @FXML private HBox previewBox;

    @FXML private Slider contrastSlider;
    @FXML private Slider brightnessSlider;

    @FXML private CheckBox grayscaleCheckbox;

    @FXML private TextField nameField;

    @FXML private ImageView postImage;

    @FXML private ToggleButton previewToggle;

    @FXML private Label contrastLabel;
    @FXML private Label brightnessLabel;
    @FXML private Label errorLabel;

    private BufferedImage bi;

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
    private void onSave(ActionEvent e){
        Button button = (Button) e.getSource();
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
        button.disableProperty().bind(create_profile_task.runningProperty());

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
        bi = SwingFXUtils.fromFXImage(postImage.getImage(), null);
        this.errorLabel.setStyle("-fx-text-fill: red");
        contrastLabel.textProperty().bindBidirectional(contrastSlider.valueProperty(), new NumberStringConverter("###.#"));
        brightnessLabel.textProperty().bindBidirectional(brightnessSlider.valueProperty(), new NumberStringConverter("###.#"));

        contrastSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                applyProfile();
            }
        });

        brightnessSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                applyProfile();
            }
        });

        grayscaleCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            applyProfile();
        });

        previewToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            previewBox.setVisible(newValue);
            previewBox.setManaged(newValue);
        });

        this.errorLabel.setOpacity(0);
    }

    private void applyProfile(){
        BufferedImage processedImage = ImageProcessor.applyProfileSettings(bi, new Profile("Dummy", brightnessSlider.getValue(), contrastSlider.getValue(), grayscaleCheckbox.isSelected()));
        Image img = SwingFXUtils.toFXImage(processedImage, null);
        postImage.setImage(img);
    }

}
