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
        Connection con = null;
        try {
            con = ConnectionManager.getDataSource().getConnection();
            con.setAutoCommit(false);
            int id = this.documentRepository.insertDocument(con, document);
            if (id < 0){
                throw new MyException("Failed to insert document");
            }
            this.fileRepository.saveFiles(con, id, document.getFiles());
            con.commit();
        }
        catch (SQLException ex) {
            if (con != null){
                try {
                    con.rollback();
                }
                catch (SQLException e) {
                    logger.error("Failed to rollback transaction", e);
                }
            }
            else{
                logger.error("Failed to get a database connection due to: {}", ex.getMessage());
            }
            throw new MyException("Sorry something went wrong when saving document");
        }

        finally {
            if (con != null){
                try {
                    con.close();
                }
                catch (SQLException e) {
                    logger.error("Failed to close database connection", e);
                }
            }
        }
    }
}
