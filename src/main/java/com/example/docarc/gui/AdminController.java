package com.example.docarc.gui;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AdminController implements Initializable {

    @FXML private VBox sideBar;
    @FXML private VBox userManagementBox;
    @FXML private StackPane contentBox;
    @FXML private StackPane listContainer;

    @FXML private Label adminNameLabel;
    @FXML private Label welcomeUserLabel;
    @FXML private Label logsLabel;

    @FXML private Button userManBtn;
    @FXML private Button metadataBtn;
    @FXML private Button ActivityLogBtn;
    @FXML private Button appLogsBtn;
    @FXML private Button errorLogsBtn;
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;

    @FXML private Region menuToggleBtn;
    @FXML private Button logOutButton;

    @FXML private TableView<ParentUser> usersTable;
    @FXML private TableColumn<ParentUser, String> userUsernameColumn;
    @FXML private TableColumn<ParentUser, Role> userRoleColumn;
    @FXML private TableColumn<ParentUser, Void> actionsColumn;

    @FXML private ListView<String> appLogsList;
    @FXML private ListView<String> errorLogsList;

    private ObservableList<ParentUser> usersLst = FXCollections.observableArrayList();
    private ObservableList<String> observableAppLogs = FXCollections.observableArrayList();
    private ObservableList<String> observableErrorLogs = FXCollections.observableArrayList();

    private boolean isMenuOpen = false;

    private UserService userService;
    private LogService logService;
    private Timeline timeLine;
    private Admin user;

    public AdminController() {
        this.userService = new UserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.logService = new LogService();
        setUpUserTable();
        setUpTimeline();
        setUpLogs();
        refreshLogs();
    }

    private void setUpTimeline() {
        this.timeLine = new Timeline(new KeyFrame(Duration.seconds(14), e -> sendLogs()));
        this.timeLine.setCycleCount(Timeline.INDEFINITE);
        this.timeLine.play();
    }

    private void refreshLogs(){
        Task<HashMap<String, List<String>>> task = new Task<>() {
            @Override
            protected HashMap<String, List<String>> call() throws Exception {
                List<String> appLogs = logService.getAppLogs();
                List<String> errorLogs = logService.getErrorLogs();
                HashMap<String, List<String>> map = new HashMap<>();
                map.put("appLogs", appLogs);
                map.put("errorLogs", errorLogs);
                return map;
            }
        };

        task.setOnSucceeded(event -> {
            HashMap<String, List<String>> map = task.getValue();
            List<String> appLogs = map.get("appLogs");
            List<String> errorLogs = map.get("errorLogs");

            observableAppLogs.setAll(appLogs);
            observableErrorLogs.setAll(errorLogs);
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    private void setUpLogs() {
        appLogsList.setItems(observableAppLogs);
        errorLogsList.setItems(observableErrorLogs);
    }

    public void setUser(Admin usr) {
        this.user = usr;
        adminNameLabel.setText(usr.getUsername());
        welcomeUserLabel.setText(usr.getUsername());
        refreshUserTable();
    }

    @FXML
    private void logOut(){
        Stage st = (Stage) logOutButton.getScene().getWindow();
        st.close();
        try {
            UIHelper.logOut();
        } catch (IOException e) {
            return;
        }

    }

    private void setUpUserTable(){
        this.userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        this.userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        actionsColumn.setCellFactory((column) -> new TableCell<ParentUser, Void>() {
            private final HBox hbox = new HBox();
            private final Region editIcon = new Region();
            private final Region deleteIcon = new Region();

            {
                editIcon.getStyleClass().add("btn-icon-edit");
                deleteIcon.getStyleClass().add("btn-icon-delete");
                deleteIcon.getStyleClass().add("destructive-c");

                editIcon.setMinHeight(16);
                editIcon.setMinWidth(16);

                deleteIcon.setPrefHeight(16);
                deleteIcon.setPrefWidth(16);

                hbox.setSpacing(20);
                hbox.setAlignment(Pos.CENTER);
                hbox.getChildren().addAll(editIcon, deleteIcon);

                deleteIcon.setOnMouseClicked(event -> {
                    ParentUser user = this.getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });

                editIcon.setOnMouseClicked(event -> {
                    ParentUser user = this.getTableView().getItems().get(getIndex());
                    onEditUser(user);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText(null);
                    setGraphic(null);
                }else{
                    setText(null);
                    setGraphic(hbox);
                }
            }
        });
        this.usersTable.setItems(usersLst);
    }

    private void changeView(String target, StackPane parent){
        for(Node n : parent.getChildren()){
            if (n.getId().equals(target)){
                n.setVisible(true);
            }else{
                n.setVisible(false);
            }
        }
    }

    @FXML
    private void onUserManClick(ActionEvent actionEvent) {
        changeView("userManagementBox", contentBox);
    }

    @FXML
    private void onMetadataClick(ActionEvent actionEvent) {
        System.out.println("Metadata clicked");
    }

    @FXML
    private void onLogsClick(ActionEvent actionEvent) {
        refreshLogs();
        changeView("activityLogsBox", contentBox);
    }

    @FXML
    private void onAppLogsClick() {
        logsLabel.setText(appLogsBtn.getText());
        appLogsBtn.setDisable(true);
        errorLogsBtn.setDisable(false);
        changeView("appLogsList", listContainer);
    }

    @FXML
    private void onErrorLogsClick() {
        logsLabel.setText(errorLogsBtn.getText());
        appLogsBtn.setDisable(false);
        errorLogsBtn.setDisable(true);
        changeView("errorLogsList", listContainer);
    }

    @FXML
    private void menuSlide(MouseEvent mouseEvent) {
        UIHelper.sideBarAnimation(isMenuOpen, sideBar, () -> isMenuOpen = !isMenuOpen);
    }

    @FXML
    private void onAddUser(){
        add_edit_user(null);
    }

    @FXML
    private void onEditUser(ParentUser user){
        if (user == null) return;
        add_edit_user(user);
    }

    private void add_edit_user(ParentUser selectedUser) {
        String filename = "add_edit_user.fxml";
        String title = (selectedUser == null) ? "Add User" : "Edit User";
        try {
                Object obj = UIHelper.openNewWindow(filename, title, true);
                AddEditUserController addEditController = (AddEditUserController) obj;
                addEditController.setController(this);
                if (selectedUser == null) return;
                addEditController.setUser(selectedUser);
        }
        catch (Exception e) {
            System.out.println("here either needs to be an alert or some error label");
        }
    }


    @FXML
    private void deleteUser(ParentUser selectedUser) {
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