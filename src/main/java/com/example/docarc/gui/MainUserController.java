package com.example.docarc.gui;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.User;
import com.example.docarc.bll.DataService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainUserController implements Initializable {


    @FXML private ScrollPane boxesArea;
    @FXML private VBox boxCardsArea;

    private User user;
    private DataService dataService;
    private Timeline timeline;

    public void setUser(User usr){
        this.user = usr;
        loadData();
    }

    public MainUserController(){
        this.dataService = new DataService();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(15), ev -> loadData()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }

    private void loadData(){
        Task<List<Box>> getBoxes = new Task<List<Box>>() {
            @Override
            protected List<Box> call() throws Exception {
                return dataService.getUserBoxes(user);
            }
        };
        getBoxes.setOnSucceeded(event -> {
            try {
                displayBoxCards(getBoxes.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        getBoxes.setOnFailed(event -> {
            Throwable exception = getBoxes.getException();
            System.out.println("exception: " + exception.getMessage());
        });
        new Thread(getBoxes).start();
    }

    private void displayBoxCards(List<Box> boxes) throws IOException {
        this.boxCardsArea.getChildren().clear();
        boxesArea.setContent(boxCardsArea);
        int i = 1;
        HBox hBox = new HBox(70);
        for (Box box : boxes) {
            if (i > 3) {
                boxCardsArea.getChildren().add(hBox);
                hBox = new HBox();
                i = 1;
            }
            VBox boxCard = createBoxCard(box);
            hBox.getChildren().add(boxCard);
            i++;
        }
        if (!hBox.getChildren().isEmpty()) {
            boxCardsArea.getChildren().add(hBox);
        }
    }

//    public void displayUploadedDocument(Document doc){
//        try {
//            UIHelper.displayDocument(doc);
//        }
//        catch (Exception e){
//            System.out.println("sorry");
//        }
//    }

    private VBox createBoxCard(Box box) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("box_card.fxml"));
        Parent node = loader.load();
        BoxCardController boxCardController = loader.getController();
        boxCardController.setData(box);
        return (VBox) node;
    }

    private VBox createDocumentCard(Document document){
        VBox documentCard = new VBox(15);
        documentCard.setPrefWidth(280);
        documentCard.setStyle(" -fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow( three-pass-box, rgba(0,0,0,0.1), 10,0,0,4); ");
        Label title = new Label(document.getName());
        title.setStyle(" -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");
        Label filesAmount = new Label(document.getFiles().size() + " files");
        filesAmount.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
        Label createdDate = new Label(document.getCreatedDate());
        createdDate.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        documentCard.getChildren().addAll(title,filesAmount,createdDate);
        return documentCard;
    }
}
