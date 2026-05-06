package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class UploadTemplateCont {

    private User user;

    public void setUser(User usr){
        this.user = usr;
    }

    public void loadDocumentsForBox() {
        System.out.println("aga nu");
    }

    public void loadAllDocuments() {
        System.out.println("aga nu 2");
    }

    @FXML
    private void createBox(){
        String fileName = "create_box.fxml";
        String title = "Create Box";
        try {
            Object obj = UIHelper.openNewWindow(fileName, title, true);
            ((CreateBoxController) obj).setUser(user);
        } catch (IOException e) {
            return;//needs to be a notification apparently
        }

    };
}
