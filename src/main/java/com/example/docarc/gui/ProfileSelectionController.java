package com.example.docarc.gui;

import com.example.docarc.be.Profile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;

import java.util.List;

public class ProfileSelectionController {

    @FXML private ComboBox<Profile> profileComboBox;
    @FXML private DialogPane dialogPane;

    private List<Profile> profiles;

    @FXML
    public void initialize() {
        profileComboBox.setConverter(new javafx.util.StringConverter<Profile>() {
            @Override
            public String toString(Profile profile) {
                return profile != null ? profile.getName() : "";
            }

            @Override
            public Profile fromString(String string) {
                return profileComboBox.getItems().stream()
                        .filter(profile -> profile.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
        profileComboBox.setItems(FXCollections.observableArrayList(profiles));

        // Выбираем Default профайл по умолчанию если он есть
        if (!profiles.isEmpty()) {
            Profile defaultProfile = profiles.stream()
                    .filter(p -> "Default".equals(p.getName()))
                    .findFirst()
                    .orElse(profiles.get(0));
            profileComboBox.setValue(defaultProfile);
        }
    }

    public Profile getSelectedProfile() {
        return profileComboBox.getValue();
    }

    public boolean isProfileSelected() {
        return profileComboBox.getValue() != null;
    }

    public ComboBox<Profile> getProfileComboBox() {
        return profileComboBox;
    }
}
