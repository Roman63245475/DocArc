package com.example.docarc.bll;

import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.impl.DocumentRepository;
import com.example.docarc.repo.impl.FileRepository;
import com.example.docarc.repo.repositories.IDocumentRepository;
import com.example.docarc.repo.repositories.IFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DocumentFileService {

    private IDocumentRepository documentRepository;
    private IFileRepository fileRepository;
    private final Logger logger = LoggerFactory.getLogger(DocumentFileService.class);
    private ConnectionManager connectionManager;
    public DocumentFileService() {
        this.documentRepository = new DocumentRepository();
        this.fileRepository = new FileRepository();
    }

    private void setUpFiles(Document doc) throws IOException {
        for (Tiff file : doc.getFiles()) {
            file.setFileContent(Files.readAllBytes(file.getFile().toPath()));
        }
    }

    public void saveDocument(Document document) throws MyException, IOException {
        setUpFiles(document);
        documentRepository.saveDocument(document);
    }

    public void onEditDocument(Document document) throws MyException {
        if (document == null) {
            throw new MyException("Could not find document");
        }
        documentRepository.updateDocument(document);
    }
}
