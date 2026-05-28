package com.example.docarc.gui;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import com.example.docarc.bll.DataService;
import com.example.docarc.bll.DocumentFileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MoveFileController implements Initializable {

    @FXML private ComboBox<Box> boxCombobox;
    @FXML private ComboBox<Document> docCombobox;
    @FXML private ListView<Tiff> filesListView;
    @FXML private Label errorLabel;

    private Document sourceDocument;
    private Tiff file_to_move;
    private User user;
    private Tiff draggedItem;
    private DocumentFileService documentFileService;
    private DocumentViewController parentController;

    private DataService dataService;
    private ObservableList<Box> availableBoxes = FXCollections.observableArrayList();
    private ObservableList<Document> availableDocuments = FXCollections.observableArrayList();
    private ObservableList<Tiff> files =  FXCollections.observableArrayList();

    @FXML private void onSave(){
        Document targetDocument = docCombobox.getSelectionModel().getSelectedItem();
        if(targetDocument == null){
            return;
        }
        if (files.isEmpty()){
            return;
        }
        List<Tiff> changedSequence = new ArrayList<>();
        int order_id = 1;
        for(Tiff t : filesListView.getItems()){
            t.setOrderId(order_id);
            changedSequence.add(t);
            order_id++;
        }
        saveChangedFilesSecondPart(targetDocument, changedSequence);
    }

    @FXML private void onCancel(){
        Stage stage = (Stage) filesListView.getScene().getWindow();
        stage.close();
    }

    public void setData(Document document, Tiff file_to_move, User user, DocumentViewController documentViewController){
        this.sourceDocument = document;
        this.file_to_move = file_to_move;
        this.user = user;
        this.parentController = documentViewController;
        displayAvailableBoxes();
    }

    private void saveChangedFilesSecondPart(Document document, List<Tiff> changedSequence){
        Task<Void> save_changed_files_task = new  Task<Void>() {
            @Override
            public Void call() throws Exception {
                documentFileService.saveChangedFiles(document, changedSequence);
                return null;
            }
        };
        save_changed_files_task.setOnSucceeded(e -> {
            onCancel();
        });
        save_changed_files_task.setOnFailed(e -> {
            onCancel();
            System.out.println(save_changed_files_task.getException().getMessage());
            save_changed_files_task.getException().printStackTrace();
        });
        new Thread(save_changed_files_task).start();
    }

    private void displayAvailableBoxes(){
        Task<List<Box>> get_available_boxes_task = new Task<List<Box>>(){
            @Override
            protected List<Box> call() throws Exception {
                return dataService.getAvailableBoxes(user);
            }
        };
        get_available_boxes_task.setOnSucceeded(event -> {
            this.availableBoxes.setAll(get_available_boxes_task.getValue());
        });
        get_available_boxes_task.setOnFailed(event -> {
            onCancel();
        });
        new Thread(get_available_boxes_task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabel.setText("");
        this.errorLabel.setStyle("-fx-text-fill: red");
        this.documentFileService = new DocumentFileService();
        setUpListView();
        this.dataService = new DataService();
        this.boxCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.files.clear();
            this.availableDocuments.clear();
            displayAvailableDocuments(newValue);
        });
        this.docCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.files.clear();
            displayDocumentFiles(newValue);
        });
        this.boxCombobox.setItems(availableBoxes);
        this.docCombobox.setItems(availableDocuments);
        this.filesListView.setItems(files);
    }

    private void displayAvailableDocuments(Box box){
        Task<List<Document>> get_available_documents = new Task<List<Document>>(){
            @Override
            protected List<Document> call() throws Exception {
                return dataService.getAvailableDocuments(box, sourceDocument);
            }
        };
        get_available_documents.setOnSucceeded(event -> {
            this.availableDocuments.setAll(get_available_documents.getValue());
        });
        get_available_documents.setOnFailed(event -> {
            onCancel();
        });
        new Thread(get_available_documents).start();
    }

    private void displayDocumentFiles(Document document){
        Task<List<Tiff>> get_files_task = new Task<List<Tiff>>(){
            @Override
            protected List<Tiff> call() throws Exception {
                return dataService.getFilesByDocument(document);
            }
        };
        get_files_task.setOnSucceeded(event -> {
            List<Tiff> task_files = get_files_task.getValue();
            task_files.add(file_to_move);
            files.setAll(task_files);
        });
        get_files_task.setOnFailed(event -> {
            onCancel();
        });
        new Thread(get_files_task).start();
    }

    private void showFile(Tiff tiff){
        try {
            UIHelper.showFile(tiff);
        }
        catch (IOException e){
            return;
        }
    }

    private void setUpListView() {

        filesListView.setCellFactory(listView -> {

            ListCell<Tiff> cell = new ListCell<>() {

                private final HBox hbox = new HBox();

                private final Label label = new Label();

                private final Region stretcher = new Region();
                private final Region dotIcon = new Region();

                private final MenuButton menuButton = new MenuButton(null, dotIcon);

                {
                    // DELETE MENU ITEM
                    MenuItem view = new MenuItem("View File");
                    view.setOnAction(event -> {
                        showFile(getItem());
                    });

                    menuButton.getItems().add(view);

                    dotIcon.getStyleClass().add("icon");
                    dotIcon.setId("ellipsis-h-icon");
                    dotIcon.setMinWidth(25);
                    dotIcon.setMaxHeight(5);
                    dotIcon.setOpacity(.3);

                    HBox.setHgrow(stretcher, Priority.ALWAYS);

                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.getChildren().addAll(label, stretcher, menuButton);
                }

                @Override
                protected void updateItem(Tiff item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        label.setText(item.getFileName());
                        setGraphic(hbox);
                    }
                }
            };

            // DRAG START
            cell.setOnDragDetected(event -> {

                if (cell.isEmpty()) {
                    return;
                }

                draggedItem = cell.getItem();

                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString("drag");

                db.setContent(content);

                event.consume();
            });


            // DRAG OVER
            cell.setOnDragOver(event -> {

                if (event.getGestureSource() != cell
                        && event.getDragboard().hasString()) {

                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            });


            // DROP
            cell.setOnDragDropped(event -> {

                if (draggedItem == null) {
                    return;
                }

                ObservableList<Tiff> items = filesListView.getItems();

                int draggedIdx = items.indexOf(draggedItem);

                int thisIdx;

                if (cell.isEmpty()) {
                    thisIdx = items.size();
                } else {
                    thisIdx = cell.getIndex();
                }

                if (draggedIdx < 0) {
                    return;
                }

                items.remove(draggedIdx);

                if (thisIdx > draggedIdx) {
                    thisIdx--;
                }

                items.add(thisIdx, draggedItem);

                filesListView.getSelectionModel().select(thisIdx);

                event.setDropCompleted(true);

                draggedItem = null;

                event.consume();
            });


            // DRAG DONE
            cell.setOnDragDone(event -> {
                draggedItem = null;
                event.consume();
            });

            return cell;
        });
    }
}
