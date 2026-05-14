package com.example.docarc.gui;

import com.example.docarc.be.*;
import com.example.docarc.bll.ClientService;
import com.example.docarc.bll.LogService;
import com.example.docarc.bll.ProfileService;
import com.example.docarc.bll.UserService;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
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
    @FXML private VBox clientManagementView;

    @FXML private Region questionIcon;

    @FXML private Label adminNameLabel;
    @FXML private Label welcomeUserLabel;
    @FXML private Label logsLabel;

    @FXML private Button userManBtn;
    @FXML private Button profileManagementButton;
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

    @FXML private TableView<Profile> profilesTable;
    @FXML private Button assignProfileToClientBtn;
    @FXML private TableColumn<Profile, String> profileNameColumn;
    @FXML private TableColumn<Profile, Double> brightnessProfileColumn;
    @FXML private TableColumn<Profile, Double> contrastProfileColumn;
    @FXML private TableColumn<Profile, Boolean> grayscaleProfileColumn;

    //clients table
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, String> clientCountryColumn;
    @FXML private TableColumn<Client, String> clientCityColumn;
    @FXML private TableColumn<Client, Integer> clientUserAmountColumn;
    private ObservableList<Client> clientsList = FXCollections.observableArrayList();

    // clients combobox
    @FXML private ComboBox<Client> clientsComboBox;


    private ObservableList<ParentUser> usersLst = FXCollections.observableArrayList();
    private ObservableList<String> observableAppLogs = FXCollections.observableArrayList();
    private ObservableList<String> observableErrorLogs = FXCollections.observableArrayList();
    private ObservableList<Profile> observableProfiles = FXCollections.observableArrayList();

    private boolean isMenuOpen = false;

    private UserService userService;
    private LogService logService;
    private ProfileService profileService;
    private Timeline timeLine;
    private Admin user;
    private ClientService clientService;
    private Client selectedClient;

    public AdminController() {
        this.userService = new UserService();
        this.logService = new LogService();
        this.profileService = new ProfileService();
        this.clientService = new ClientService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTooltip();
        setUpUserTable();
        setUpTimeline();
        setUpLogs();
        setUpProfilesTable();
        assignProfileToClientBtn.disableProperty().bind(
                profilesTable.getSelectionModel().selectedItemProperty().isNull());
        refreshLogs();
        displayProfiles();
        setUpClientsTable();
        setUpClientsCombobox();
        displayClients();
        //Shortcuts
        sideBar.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            newValue.setOnKeyPressed(event -> {
                ParentUser selectedUser = usersTable.getSelectionModel().getSelectedItem();

                String activeTab = "";

                for (Node n : contentBox.getChildren()) {
                    if (n.isVisible() && n.getId() != null){
                        activeTab = n.getId();
                        break;
                    }
                }

                if (event.getCode() == KeyCode.A) {
                    if ("userManagementBox".equals(activeTab)) onAddUser();
                    if("profileManagementView".equals(activeTab)) createProfile();
                    if("clientManagementView".equals(activeTab)) createClient();
                }

                if (event.getCode() == KeyCode.S) {
                    if ("profileManagementView".equals(activeTab)) assignProfileToClientClick();
                }

                if (event.getCode() == KeyCode.U) onUserManClick();
                if (event.getCode() == KeyCode.L) onLogsClick();
                if (event.getCode() == KeyCode.P) profileManagementButtonClick();
                if (event.getCode() == KeyCode.C) onClientManagementClick();

                if (selectedUser != null && "userManagementBox".equals(activeTab)) {
                    if (event.getCode() == KeyCode.E) onEditUser(selectedUser);
                    if (event.getCode() == KeyCode.D) deleteUser(selectedUser);
                }
            });
        });
    }

    private void setUpClientsCombobox(){
        this.clientsComboBox.setItems(clientsList);
        this.clientsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedClient = newValue;
            displayUsers();
        });
    }

    private void setUpProfilesTable(){
        this.profileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.brightnessProfileColumn.setCellValueFactory(new PropertyValueFactory<>("brightness"));
        this.contrastProfileColumn.setCellValueFactory(new PropertyValueFactory<>("contrast"));
        this.grayscaleProfileColumn.setCellValueFactory(new PropertyValueFactory<>("grayscale"));
        profilesTable.setItems(this.observableProfiles);
    }

    private void setUpClientsTable(){
        this.clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.clientCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        this.clientCityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        this.clientUserAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amountOfEmployees"));
        this.clientsTable.setItems(clientsList);
    }

    public void displayProfiles(){
        Task<List<Profile>> get_profiles_task = new Task<List<Profile>>() {
            @Override
            protected List<Profile> call() throws Exception {
                return profileService.getProfiles();
            }
        };
        get_profiles_task.setOnSucceeded(event -> {
            observableProfiles.setAll(get_profiles_task.getValue());
        });
        new Thread(get_profiles_task).start();
    }

    private void setUpTooltip() {
        Tooltip tooltip = new Tooltip();
        Label operationsLabel = new Label("Useful Shortcuts:\n" +
                "[ A ] Add a new user/profile.\n" +
                "[ E ] Edit the selected user.\n" +
                "[ D ] Delete the selected user.\n" +
                "[ S ] Open profile assignment window.\n" +
                "━━━━━━━━━━━━━━━━━\n" +
                "[ U ] Open user management tab.\n" +
                "[ P ] Open profile management tab.\n" +
                "[ L ] Open activity logs tab.");

        operationsLabel.getStyleClass().add("tooltip-label");

        tooltip.setGraphic(operationsLabel);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);

        tooltip.setOnShown(event -> {
            operationsLabel.setOpacity(0.0);
            KeyValue value = new KeyValue(operationsLabel.opacityProperty(), 1, Interpolator.EASE_BOTH);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(300), value);
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });

        Tooltip.install(questionIcon, tooltip);
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
        //refreshUserTable();
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

    @FXML
    private void createClient(){
        try {
            CreateClientController controller = (CreateClientController) UIHelper.openNewWindow("create_client_view.fxml", "Create Client", true);
            controller.setAdminUser(this.user);
            controller.setAdminController(this);
        } catch (IOException e) {
            System.out.println("needs to be logged likely");
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
            if (target.equals(n.getId())){
                n.setVisible(true);
            }else{
                n.setVisible(false);
            }
        }
    }

    @FXML
    private void onUserManClick() {
        changeView("userManagementBox", contentBox);
    }

    @FXML
    private void profileManagementButtonClick() {
        changeView("profileManagementView", contentBox);
        displayProfiles();
    }

    @FXML
    private void onLogsClick() {
        refreshLogs();
        changeView("activityLogsBox", contentBox);
    }

    @FXML
    private void assignProfileToClientClick() {
        Profile selected = profilesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        try {
            AssignProfileToClientController controller = (AssignProfileToClientController) UIHelper.openNewWindow(
                    "assign_profile_to_client_view.fxml", "Assign profile to user", true);
            controller.setProfileAndAdmin(selected, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void displayClients(){
        Task<List<Client>> get_clients = new Task<List<Client>>() {
            @Override
            protected List<Client> call() throws Exception {
                return clientService.getClients();
            }
        };
        get_clients.setOnSucceeded(e -> this.clientsList.setAll(get_clients.getValue()));
        //get_clients.setOnFailed(e -> System.out.println("idk what to do here"));
        new Thread(get_clients).start();
    }
    @FXML
    private void createProfile(){
        try {
            CreateProfileController controller = (CreateProfileController) UIHelper.openNewWindow("create_profile_view.fxml", "Create Profile", true);
            controller.setMainController(this);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

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
        if (selectedClient == null) {
            return;
        }
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
                addEditController.setClientID(selectedClient.getId());
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
                    displayUsers();
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
    @FXML
    private void onClientManagementClick(){
        System.out.println("I'm called");
        changeView("clientManagementView", this.contentBox);
    }

    public void displayUsers(){
        if (this.selectedClient == null){
            return;
        }

        Task<List<ParentUser>> getAllUsersTask = new Task<List<ParentUser>>() {

            @Override
            protected List<ParentUser> call() throws Exception {
                return userService.getAllUsersByClient(selectedClient, user.getId());
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