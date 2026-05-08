package com.example.docarc.gui;
import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import com.example.docarc.bll.ApiService;
import com.example.docarc.bll.DataService;
import javafx.beans.Observable;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class DocumentViewController implements Initializable {

    @FXML private ImageView pageView;
    @FXML private Label userLabel;
    @FXML private Button logOutButton;

    @FXML private List<Image> currentDocumentFiles;
    @FXML private ListView<Tiff> listOfFiles;

    private User user;
    private ApiService apiService;
    private DataService boxService;
    private Document document;
    private ObservableList<Tiff> files =  FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ComboBoxHelper.makeSearchable(boxChoice, [ObservableList]);
        //ComboBoxHelper.makeSearchable(folderChoice, [ObservableList]);
        this.apiService = new ApiService();
        this.boxService = new DataService();
        this.listOfFiles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println(newVal.getReference_id());
                displayImage(newVal);
            }
        });
        listOfFiles.setItems(files);
    }

    public void setDocument(Document document) {
        this.document = document;
        fillList();
        displayImage();
    }

    private void fillList() {
        files.setAll(this.document.getFiles());
    }

    private void displayImage(){
        this.pageView.setImage(SwingFXUtils.toFXImage(files.get(0).getConvertedBufferedImage(), null));
    }

    private void displayImage(Tiff file) {
        this.pageView.setImage(SwingFXUtils.toFXImage(file.getConvertedBufferedImage(), null));
    }

//    private void displayBoxes(){
//        Task<List<Box>> getBoxes= new Task<List<Box>>(){
//            @Override
//            protected List<Box> call() throws Exception {
//                return boxService.getUserBoxes(user);
//            }
//        };
//        getBoxes.setOnSucceeded((e) -> System.out.println("yest"));
//        getBoxes.setOnFailed((e) -> System.out.println("ne yest"));
//        new Thread(getBoxes).start();
//    }


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
    private void saveDocument(){
        System.out.println("I will");
    }

    @FXML
    private void onCancel(){
        Stage st = (Stage) this.logOutButton.getScene().getWindow();
        st.close();
    }

//    private void displayImages(List<Tiff> files){
//        List<Image> images = new ArrayList<>();
//        for (Tiff file : files){
//            Image image = SwingFXUtils.toFXImage(file.getConvertedBufferedImage(), null);
//            images.add(image);
//        }
//        currentDocumentFiles = images;
//        this.imageIndex = 0;
//        pageView.setImage(images.get(this.imageIndex));
//    }

//    @FXML
//    private void showPrevious(){
//        if (this.imageIndex == 0){
//            this.imageIndex = this.currentDocumentFiles.size() - 1;
//        }
//        else{
//            this.imageIndex = this.imageIndex - 1;
//        }
//        pageView.setImage(currentDocumentFiles.get(imageIndex));
//    }
//
//    @FXML
//    private void showNext(){
//        if (this.imageIndex == currentDocumentFiles.size() - 1){
//            this.imageIndex = 0;
//        }
//        else{
//            this.imageIndex = this.imageIndex + 1;
//        }
//        pageView.setImage(currentDocumentFiles.get(imageIndex));
//    }

}
