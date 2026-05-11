package com.example.docarc.gui;

import com.example.docarc.be.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class DocumentCardController {


    @FXML private Label documentName;
    @FXML private Label filesAmount;
    @FXML private Label createdDate;

    private Document document;

    public void setDocument(Document document) {
        this.document = document;
        fillFields();
    }


    private void fillFields() {
        documentName.setText(document.getName());
        filesAmount.setText(document.getFiles().size() + " files");
        createdDate.setText(document.getCreatedDate());
    }

}
