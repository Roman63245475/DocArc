package com.example.docarc.gui;
import com.example.docarc.be.Box;
import com.example.docarc.be.Folder;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import com.example.docarc.bll.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class UserController implements Initializable {

    @FXML private ImageView pageView;
    @FXML private Label userLabel;
    @FXML private ComboBox<Box> boxChoice;
    @FXML private ComboBox<Folder> folderChoice;
    @FXML private Button logOutButton;

    @FXML private List<Image> currentDocumentFiles;
    private int imageIndex;

    private User user;
    private ApiService apiService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ComboBoxHelper.makeSearchable(boxChoice, [ObservableList]);
        //ComboBoxHelper.makeSearchable(folderChoice, [ObservableList]);
        this.apiService = new ApiService();
    }

    public void onRotateLeft(){
        pageView.setRotate(pageView.getRotate() - 90);
    }

    public void onRotateRight(){
        pageView.setRotate(pageView.getRotate() + 90);
    }


    public void setUser(User user){
        this.user = user;
        this.userLabel.setText(this.user.getUsername());
    }
    @FXML
    private void logOut(){
        Stage st = (Stage) this.logOutButton.getScene().getWindow();
        st.close();
        try {
            UIHelper.logOut();
        } catch (IOException e) {
            return;
        }
    }

    @FXML
    private void loadFiles(){
        Task<List<Tiff>> task = new Task<List<Tiff>>() {
            @Override
            protected List<Tiff> call() throws Exception {
                return apiService.loadFiles();
            }
        };

        task.setOnSucceeded(event -> {
            displayImages(task.getValue());
        });
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            System.out.println(exception.getMessage());
        });
        new Thread(task).start();
    }

    private void displayImages(List<Tiff> files){
        List<Image> images = new ArrayList<>();
        for (Tiff file : files){
            Image image = SwingFXUtils.toFXImage(file.getConvertedBufferedImage(), null);
            images.add(image);
        }
        currentDocumentFiles = images;
        this.imageIndex = 0;
        pageView.setImage(images.get(this.imageIndex));
    }

    @FXML
    private void showPrevious(){
        if (this.imageIndex == 0){
            this.imageIndex = this.currentDocumentFiles.size() - 1;
        }
        else{
            this.imageIndex = this.imageIndex - 1;
        }
        pageView.setImage(currentDocumentFiles.get(imageIndex));
    }

    @FXML
    private void showNext(){
        if (this.imageIndex == currentDocumentFiles.size() - 1){
            this.imageIndex = 0;
        }
        else{
            this.imageIndex = this.imageIndex + 1;
        }
        pageView.setImage(currentDocumentFiles.get(imageIndex));
    }

}
