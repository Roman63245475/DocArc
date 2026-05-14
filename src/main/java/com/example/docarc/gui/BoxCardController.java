package com.example.docarc.gui;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.bll.ApiService;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BoxCardController {


    @FXML private VBox boxCard;
    @FXML private Label boxTitle;
    @FXML private Label documentsAmount;
    @FXML private ScrollPane documentsArea;

    private Box box;
    private ApiService apiService;
    private User currentUser;

    public BoxCardController(){
        this.apiService = new ApiService();
    }


    public void setData(Box box) throws IOException {
        this.box = box;
        fillCard();
        displayDocuments();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void fillCard(){
        this.boxTitle.setText(this.box.getName());
        this.documentsAmount.setText(this.box.getDocuments().size() + "docs");
    }

    private void displayDocuments() throws IOException {
        VBox vBox = new VBox();
        documentsArea.setContent(vBox);
        for (Document document : box.getDocuments()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("document_card.fxml"));
            Parent node = loader.load();
            DocumentCardController documentController = loader.getController();
            node.setOnMouseClicked(e -> displayClickedDocument(document));
            documentController.setDocument(document);
            vBox.getChildren().add(node);
        }
    }

    private void displayClickedDocument(Document document){
        try {
            UIHelper.displayDocument(document, true);
        } catch (IOException e) {
            System.out.println("needs to be logged likely");
        }
    }


    @FXML
    private void loadDocument(){
        Task<Document> scanDocument = new Task<>() {
            @Override
            protected Document call() throws Exception {
                // Передаем имя профайла вместо "default"
                return apiService.loadDocument(box.getProfile(), box.getId());
            }
        };
        scanDocument.setOnSucceeded((e) -> {
            try {
                UIHelper.displayDocument(scanDocument.getValue(), false);
            } catch (IOException ex) {
                System.out.println("sorry couldn't display document");
            }
        });
        scanDocument.setOnFailed( (e) ->{
                    Throwable exception = scanDocument.getException();
                    System.out.println(exception.getMessage());
                }
        );
        System.out.println("I started from ui");
        new Thread(scanDocument).start();
        //System.out.println("oki");
    }

    private Optional<Profile> showProfileSelectionDialog(List<Profile> profiles) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile_selection_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            ProfileSelectionController controller = loader.getController();

            controller.setProfiles(profiles);

            Dialog<Profile> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Select Profile");

            // Добавляем кнопки
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cancelButton, applyButton);

            // Блокируем кнопку Apply если не выбран профайл
            Node applyButtonNode = dialog.getDialogPane().lookupButton(applyButton);

            applyButtonNode.setDisable(controller.getSelectedProfile() == null);

            // Включаем/выключаем кнопку Apply при изменении выбора
            controller.getProfileComboBox().getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> applyButtonNode.setDisable(newVal == null));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == applyButton) {
                    return controller.getSelectedProfile();
                }
                return null;
            });

            return dialog.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile selection dialog: " + e.getMessage());
            return Optional.empty();
        }
    }

//    private void loadDocumentWithProfile(Profile profile) {
//        Task<Document> scanDocument = new Task<>() {
//            @Override
//            protected Document call() throws Exception {
//                // Передаем имя профайла вместо "default"
//                return apiService.loadDocument(profile, box.getId());
//            }
//        };
//        scanDocument.setOnSucceeded((e) -> {
//            try {
//                UIHelper.displayDocument(scanDocument.getValue(), false);
//            } catch (IOException ex) {
//                System.out.println("sorry couldn't display document");
//            }
//        });
//        scanDocument.setOnFailed( (e) ->{
//                    Throwable exception = scanDocument.getException();
//                    System.out.println(exception.getMessage());
//                }
//        );
//        new Thread(scanDocument).start();
//    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
