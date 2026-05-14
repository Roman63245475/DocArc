package com.example.docarc.gui;
import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import com.example.docarc.bll.ApiService;
import com.example.docarc.bll.DataService;
import com.example.docarc.bll.DocumentFileService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class DocumentViewController implements Initializable {

    @FXML private ImageView pageView;
    @FXML private Label userLabel;
    @FXML private Button logOutButton;

    @FXML private List<Image> currentDocumentFiles;
    @FXML private ListView<Tiff> listOfFiles;

    private User user;
    private Document document;
    private ObservableList<Tiff> files =  FXCollections.observableArrayList();
    private Tiff draggedItem;
    private int draggedIndex;
    private DocumentFileService service;
    private DataService dataService;

    private boolean openedInEditMode = false;
    private boolean orderChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ComboBoxHelper.makeSearchable(boxChoice, [ObservableList]);
        //ComboBoxHelper.makeSearchable(folderChoice, [ObservableList]);
        this.service = new DocumentFileService();
        this.dataService = new DataService();
        this.listOfFiles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println(newVal.getReference_id());
                displayImage(newVal);
            }
        });
        listOfFiles.setItems(files);
        setListViewBehaviour();
    }

    private void setListViewBehaviour() {
        listOfFiles.setCellFactory(lv -> {

            ListCell<Tiff> cell = new ListCell<>() {

                @Override
                protected void updateItem(Tiff item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFileName());
                    }
                }
            };

            // START DRAG
            cell.setOnDragDetected(event -> {

                if (cell.isEmpty())
                    return;

                draggedItem = cell.getItem();
                draggedIndex = cell.getIndex();

                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString("tiff");

                db.setContent(content);

                event.consume();
            });

            // DRAG OVER
            cell.setOnDragOver(event -> {

                if (draggedItem != null && !cell.isEmpty()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            });

            // DROP
            cell.setOnDragDropped(event -> {

                if (draggedItem == null)
                    return;

                ObservableList<Tiff> items = listOfFiles.getItems();
                int targetIndex = cell.getIndex();

                items.remove(draggedIndex);

                items.add(targetIndex, draggedItem);

                listOfFiles.getSelectionModel().select(targetIndex);

                if (draggedIndex != targetIndex) {
                    orderChanged = true;
                }

                event.setDropCompleted(true);

                draggedItem = null;

                event.consume();
            });

            return cell;
        });
    }

    public void setDocument(Document document) {
        this.document = document;
        fillList();
        displayImage();
    }

    private void loadDocumentFiles(){
        Task<List<Tiff>> get_document_files = new Task<List<Tiff>>() {
            @Override
            protected List<Tiff> call() throws Exception {
                return dataService.getFilesByDocument(document);
            }
        };
        get_document_files.setOnSucceeded(event -> {
            this.document.setData(get_document_files.getValue());
            this.files.setAll(this.document.getFiles());
            displayImage();
        });
        new Thread(get_document_files).start();
    }

    private void fillList() {
        files.setAll(this.document.getFiles());
    }

    private void displayImage(){
        if (files.size() > 0){
            if (files.get(0).getConvertedBufferedImage() != null){
                this.pageView.setImage(SwingFXUtils.toFXImage(files.get(0).getConvertedBufferedImage(), null));
            }
        }
    }

    private void displayImage(Tiff file) {
        this.pageView.setImage(SwingFXUtils.toFXImage(file.getConvertedBufferedImage(), null));
    }

//    private void displayBoxes(){
//        Task<List<Box>> getBoxes= new Task<List<Box>>(){
//            @Override
//            protected List<Box> call() throws Exception {
//                return boxService.getUserBoxes(user);
//            }
//        };
//        getBoxes.setOnSucceeded((e) -> System.out.println("yest"));
//        getBoxes.setOnFailed((e) -> System.out.println("ne yest"));
//        new Thread(getBoxes).start();
//    }


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
    private void saveDocument(){
        List<Tiff> finalOrder = new ArrayList<>();
        int orderId = 1;
        for (Tiff t : listOfFiles.getItems()) {
            t.setReference_id(orderId);
            finalOrder.add(t);
            orderId++;
        }
        this.document.setData(finalOrder);
        if (openedInEditMode){
            onEditDocument();
        }else{
            saveDocumentSecondPart();
        }
    }

    private void saveDocumentSecondPart(){
        Task<Void> save_document_task = new  Task<Void>() {
            @Override
            protected Void call() throws Exception {
                service.saveDocument(document);
                return null;
            }
        };
        save_document_task.setOnSucceeded(event -> {
            onCancel();
        });
        save_document_task.setOnFailed(event -> {
            Throwable exception = save_document_task.getException();
            System.out.println("have to do something special with this");
            System.out.println(exception.getMessage());
            onCancel();
        });
        new Thread(save_document_task).start();
    }

    private void onEditDocument(){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                service.onEditDocument(document);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            onCancel();
        });

        task.setOnFailed(event -> {
            System.out.println("Error soobshenie: " + task.getException().getMessage());
            onCancel();
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void setEditMode(){
        this.openedInEditMode = true;
        loadDocumentFiles();
    }

    @FXML
    private void onCancel(){
        Stage st = (Stage) this.logOutButton.getScene().getWindow();
        st.close();
    }

//    private void displayImages(List<Tiff> files){
//        List<Image> images = new ArrayList<>();
//        for (Tiff file : files){
//            Image image = SwingFXUtils.toFXImage(file.getConvertedBufferedImage(), null);
//            images.add(image);
//        }
//        currentDocumentFiles = images;
//        this.imageIndex = 0;
//        pageView.setImage(images.get(this.imageIndex));
//    }

//    @FXML
//    private void showPrevious(){
//        if (this.imageIndex == 0){
//            this.imageIndex = this.currentDocumentFiles.size() - 1;
//        }
//        else{
//            this.imageIndex = this.imageIndex - 1;
//        }
//        pageView.setImage(currentDocumentFiles.get(imageIndex));
//    }
//
//    @FXML
//    private void showNext(){
//        if (this.imageIndex == currentDocumentFiles.size() - 1){
//            this.imageIndex = 0;
//        }
//        else{
//            this.imageIndex = this.imageIndex + 1;
//        }
//        pageView.setImage(currentDocumentFiles.get(imageIndex));
//    }

}
