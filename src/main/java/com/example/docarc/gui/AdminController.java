package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.be.User;
import com.example.docarc.bll.LogService;
import com.example.docarc.bll.UserService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;

    private Admin user;
    private boolean isMenuOpen = false;
    private ObservableList<ParentUser> usersLst = FXCollections.observableArrayList();
    private UserService userService;
    private Timeline timeLine;
    private LogService logService;
    private ObservableList<String> appLogsLst = FXCollections.observableArrayList();
    private ObservableList<String> errorLogsLst = FXCollections.observableArrayList();

    public AdminController() {
        this.userService = new UserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpUserTable();
        this.logService = new LogService();
        setUpTimeline();
        setUpLogs();
    }

    private void setUpTimeline() {
        this.timeLine = new Timeline(new KeyFrame(Duration.seconds(14), e -> sendLogs()));
        this.timeLine.setCycleCount(Timeline.INDEFINITE);
        this.timeLine.play();
    }

    private void setUpLogs() {

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
    private void add_edit_user(ActionEvent actionEvent) {
        String filename = "add_edit_user.fxml";
        String title = (actionEvent.getSource() == addUserButton) ? "Add User" : "Edit User";
        try {
            if (actionEvent.getSource() == addUserButton) {
                Object obj = UIHelper.openNewWindow(filename, title, true);
                AddEditUserController addEditController = (AddEditUserController) obj;
                addEditController.setController(this);
            }
            else{
                ParentUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
                if (selectedUser == null) {
                    return;
                    //must be some alert to notify user what is the problem in
                }
                Object obj = UIHelper.openNewWindow(filename, title, true);
                AddEditUserController addEditController = (AddEditUserController) obj;
                addEditController.setController(this);
                addEditController.setUser(selectedUser);
            }

        }
        catch (Exception e) {
            System.out.println("here either needs to be an alert or some error label");
        }
    }


    @FXML
    private void deleteUser(ActionEvent actionEvent) {
        ParentUser selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;
        try {
            Consumer<AlertController> codeToExecute = (controller) -> {
                //Consumer acts as a small method to execute.
                //We use consumer so we can pass what needs to be executed before the window opens (f.ex Change the text).
                controller.setText("Deletion Confirmation", "Are you sure you want to delete " + selectedUser.getUsername() + "?");
            };
            AlertController controller = UIHelper.openAndWait("alert-view.fxml", "Confirm Deletion", codeToExecute);
            if (controller.isConfirmed()){
                Task<Void> task = new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {
                        userService.deleteUser(selectedUser.getId());
                        return null;
                    }
                };

                task.setOnSucceeded(event -> {
                    refreshUserTable();
                });

                task.setOnFailed(event -> {
                    //Notify user if something went wrong
                });

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void sendLogs(){
        new Thread(() -> {this.logService.sendLogs();}).start();
    }

}