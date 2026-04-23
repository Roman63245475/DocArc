package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class AdminController {

    @FXML private VBox sideBar;
    @FXML private VBox userManagementBox;
    @FXML private StackPane contentBox;

    @FXML private Label adminNameLabel;
    @FXML private Label welcomeUserLabel;

    @FXML private Button userManBtn;
    @FXML private Button metadataBtn;
    @FXML private Button ActivityLogBtn;

    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    @FXML private Region menuToggleBtn;

    @FXML private TableView usersTable;
    @FXML private TableColumn userUsernameColumn;
    @FXML private TableColumn userRoleColumn;

    private Admin user;
    private boolean isMenuOpen = false;

    public void setUser(Admin usr) {
        this.user = usr;
        if (adminNameLabel != null) adminNameLabel.setText(usr.getUsername());
        if (welcomeUserLabel != null) welcomeUserLabel.setText(usr.getUsername());
    }


    @FXML
    private void onUserManClick(ActionEvent actionEvent) {
        System.out.println("User Management clicked");
    }

    @FXML
    private void onMetadataClick(ActionEvent actionEvent) {
        System.out.println("Metadata clicked");
    }

    @FXML
    private void onLogsClick(ActionEvent actionEvent) {
        System.out.println("Activity Log clicked");
    }

    @FXML
    private void menuSlide(MouseEvent mouseEvent) {
        UIHelper.sideBarAnimation(isMenuOpen, sideBar, () -> isMenuOpen = !isMenuOpen);
    }

    @FXML
    private void addUser() {
        System.out.println("Add user clicked");
    }

    @FXML
    private void editUser(ActionEvent actionEvent) {
        System.out.println("Edit user clicked");
    }

    @FXML
    private void deleteUser(ActionEvent actionEvent) {
        System.out.println("Delete user clicked");
    }
}