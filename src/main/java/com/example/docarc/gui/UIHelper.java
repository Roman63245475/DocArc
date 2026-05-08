package com.example.docarc.gui;

import com.example.docarc.be.Document;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.Consumer;

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

    public static Object openNewWindow(String fileName, String title, Boolean mod) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIHelper.class.getResource(fileName));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        //loader.getController()
        if (mod){
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.setTitle(title);
        stage.show();
        return loader.getController();
    }

    public static void displayDocument(Document doc) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIHelper.class.getResource("document_view_page.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        DocumentViewController documentViewController = loader.getController();
        documentViewController.setDocument(doc);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //<T> acts as placeholder for any object type. Because we pass our consumer
    //when executing this method, Java automaticaly understands what type to return.
    public static <T> T openAndWait(String fileName, String title, Consumer<T> codeToExecute) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIHelper.class.getResource(fileName));
        // controller появляется только после loader.load()
        Parent root = loader.load();
        T controller = loader.getController();
        if (codeToExecute != null) {
            codeToExecute.accept(controller);
        }
        // повторно loader.load() нельзя: используем уже загруженный root
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return controller;
    }

    public static void logOut() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UIHelper.class.getResource("login_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }



}
