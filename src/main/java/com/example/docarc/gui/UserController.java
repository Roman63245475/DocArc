package com.example.docarc.gui;
import com.example.docarc.be.Box;
import com.example.docarc.be.Folder;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UserController implements Initializable {

    @FXML private ImageView pageView;
    @FXML private Label userLabel;
    @FXML private ComboBox<Box> boxChoice;
    @FXML private ComboBox<Folder> folderChoice;
    @FXML private Button logOutButton;

    @FXML private ListView<Tiff> fileList;

    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ComboBoxHelper.makeSearchable(boxChoice, [ObservableList]);
        //ComboBoxHelper.makeSearchable(folderChoice, [ObservableList]);
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
    private void test_user(){

    }
}
