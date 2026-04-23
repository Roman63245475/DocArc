package com.example.docarc.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UIHelper {

    @FXML
    public static void sideBarAnimation(boolean isMenuOpen, VBox sideBar, Runnable r1) {
        double targetWidth = isMenuOpen ? 0.0 : 210.0;

        if (!isMenuOpen) {
            sideBar.setVisible(true);
            sideBar.setManaged(true);
        }
        KeyValue kvPref = new KeyValue(sideBar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(250), kvPref);
        Timeline timeline = new Timeline(keyFrame);

        timeline.setOnFinished(e -> {
            if (isMenuOpen) {
                sideBar.setVisible(false);
                sideBar.setManaged(false);
            }
            r1.run();
        });

        timeline.play();
    }
}
