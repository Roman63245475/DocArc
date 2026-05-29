package com.example.docarc.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;

public class ShowImageController {


    @FXML private ImageView pictureField;
    @FXML private VBox parentNode;


    public void setImage(BufferedImage img){
        Image image = SwingFXUtils.toFXImage(img,null);
        pictureField.fitWidthProperty().bind(parentNode.widthProperty());
        pictureField.fitHeightProperty().bind(parentNode.heightProperty());
        pictureField.setImage(image);
    }
}
