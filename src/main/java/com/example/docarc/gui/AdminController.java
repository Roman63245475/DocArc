package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class AdminController {
    @FXML private VBox sideBar;


    private Admin user;
    private boolean isMenuOpen = false;



    public void setUser(Admin usr){
        this.user = usr;
    }

    @FXML
    private void onUserManClick(ActionEvent actionEvent) {
        System.out.println("odhr");
    }

    @FXML
    private void onEventManClick(ActionEvent actionEvent) {
        System.out.println("odhr");
    }

    @FXML
    private void menuSlide(MouseEvent mouseEvent) {
        UIHelper.sideBarAnimation(isMenuOpen, sideBar, () -> isMenuOpen = !isMenuOpen);
    }

    @FXML
    private void addUser(){
        System.out.println("hey");
    }

    @FXML
    private void editUser(ActionEvent actionEvent) {
        System.out.println("odhr");
    }

    @FXML
    private void deleteUser(ActionEvent actionEvent) {
        System.out.println("odhr");
    }

    @FXML
    private void assignCoordinator(){
        System.out.println("hey");
    }

    @FXML
    private void editEvent(ActionEvent actionEvent) {
        System.out.println("khrfiu");
    }

    @FXML
    private void deleteEvent(ActionEvent actionEvent) {
        System.out.println("h");
    }
}
