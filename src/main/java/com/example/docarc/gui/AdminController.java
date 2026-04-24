package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.be.User;
import com.example.docarc.bll.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private VBox sideBar;
    @FXML private VBox userManagementBox;
    @FXML private StackPane contentBox;

    @FXML private Label adminNameLabel;
    @FXML private Label welcomeUserLabel;

    @FXML private Button userManBtn;
    @FXML private Button metadataBtn;
    @FXML private Button ActivityLogBtn;



    @FXML private Region menuToggleBtn;

    @FXML private TableView<ParentUser> usersTable;
    @FXML private TableColumn<ParentUser, String> userUsernameColumn;
    @FXML private TableColumn<ParentUser, Role> userRoleColumn;

    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    private Admin user;
    private boolean isMenuOpen = false;
    private ObservableList<ParentUser> usersLst = FXCollections.observableArrayList();
    private UserService userService;

    public AdminController() {
        this.userService = new UserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpUserTable();
    }

    public void setUser(Admin usr) {
        this.user = usr;
        adminNameLabel.setText(usr.getUsername());
        welcomeUserLabel.setText(usr.getUsername());
        refreshUserTable();
    }

    private void setUpUserTable(){
        this.userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        this.userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        this.usersTable.setItems(usersLst);
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
        String filename = "add_edit_user.fxml";
        String title = "Add User";
        try {
            Object obj = UIHelper.openNewWindow(filename, title, true);
            AddEditUserController addEditController = (AddEditUserController) obj;
            addEditController.setController(this);
        }
        catch (Exception e) {
            System.out.println("here either needs to be an alert or some error label");
        }
    }

    @FXML
    private void editUser(ActionEvent actionEvent) {
        System.out.println("Edit user clicked");
    }

    @FXML
    private void deleteUser(ActionEvent actionEvent) {
        System.out.println("Delete user clicked");
    }

    public void refreshUserTable(){
        Task<List<ParentUser>> getAllUsersTask = new Task<List<ParentUser>>() {

            @Override
            protected List<ParentUser> call() throws Exception {
                return userService.getAllUsers(user.getId());
            }
        };
        getAllUsersTask.setOnSucceeded(event -> {
            this.usersLst.setAll(getAllUsersTask.getValue());
        });
        getAllUsersTask.setOnFailed(event -> {
            System.out.println("some maybe alert needs to be displayed");
            System.out.println(getAllUsersTask.getValue());
            getAllUsersTask.getException().printStackTrace();
        });
        new Thread(getAllUsersTask).start();
    }

}