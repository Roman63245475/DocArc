package com.example.docarc.gui;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.bll.ApiService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class BoxCardController {


    @FXML private VBox boxCard;
    @FXML private Label boxTitle;
    @FXML private Label documentsAmount;
    @FXML private ScrollPane documentsArea;

    private Box box;
    private ApiService apiService;

    public BoxCardController(){
        this.apiService = new ApiService();
    }


    public void setData(Box box) throws IOException {
        this.box = box;
        fillCard();
        displayDocuments();
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
            UIHelper.displayDocument(document);
        } catch (IOException e) {
            System.out.println("needs to be logged likely");
        }
    }
    @FXML
    private void loadDocument(){
        Task<Document> scanDocument = new Task<>() {
            @Override
            protected Document call() throws Exception {
                return apiService.loadDocument("default", box.getDocuments().size(), box.getId());
            }
        };
        scanDocument.setOnSucceeded((e) -> {
            try {
                UIHelper.displayDocument(scanDocument.getValue());
            } catch (IOException ex) {
                System.out.println("sorry couldn't display document");
            }
        });
        scanDocument.setOnFailed( (e) ->{
                Throwable exception = scanDocument.getException();
                System.out.println(exception.getMessage());
            }
        );
        new Thread(scanDocument).start();
    }

}
