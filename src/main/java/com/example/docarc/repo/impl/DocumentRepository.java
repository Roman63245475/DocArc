package com.example.docarc.repo.impl;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentRepository implements IDocumentRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(DocumentRepository.class);

    public DocumentRepository(){
        this.ds = ConnectionManager.getDataSource();
    }
    @Override
    public List<Document> getDocumentsByBoxIds(List<Box> boxes) throws MyException {
        if (boxes.isEmpty()){
            return List.of();
        }
        List<Document> documents = new ArrayList<>();
        String placeholder = boxes.stream().map(box -> "?").collect(Collectors.joining(","));
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "select * from documents where boxId in (" + placeholder + ")";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            for (int i = 0; i < boxes.size(); i++){
                ps.setInt(i+1, boxes.get(i).getId());
            }
            ResultSet rs = ps.executeQuery();
            logger.info("Documents successfully observed");
            while (rs.next()){
                int document_id = rs.getInt("id");
                String document_name = rs.getString("name");
                int box_reference = rs.getInt("boxId");
                documents.add(new Document(document_id, document_name, box_reference));
            }
            return documents;
        }
        catch (SQLException e) {
            logger.error("Failed to observe documents due to: {}", e.getMessage());
            throw new MyException(e.getMessage());
        }
    }

//    @Override
//    public void saveDocument(Document document) throws MyException {
//        Connection con = null;
//        try {
//            con = ds.getConnection();
//            con.setAutoCommit(false);
//            String documentCreation = "insert into documents (name, reg, boxId) values (?,?,?)";
//            try (PreparedStatement ps = con.prepareStatement(documentCreation, Statement.RETURN_GENERATED_KEYS)) {
//                ps.setString(1, document.getName());
//                ps.setString(2, "");
//                ps.setInt(3, document.getBoxId());
//                ps.executeUpdate();
//                try (ResultSet rs = ps.getGeneratedKeys()) {
//                    if (!rs.next()) {
//                        throw new MyException("Failed to save document");
//                    }
//                    int generatedDocumentId = rs.getInt(1);
//                    String saveFile = "insert into files (documentId, name, orderId, file_content) values (?,?,?,?)";
//                    try (PreparedStatement ps2 = con.prepareStatement(saveFile)){
//                        for (Tiff file : document.getFiles()) {
//                            ps2.setInt(1, generatedDocumentId);
//                            ps2.setString(2, file.getFileName());
//                            ps2.setInt(3, file.getReference_id());
//                            ps2.setBytes(4, file.getFileContent());
//                            ps2.addBatch();
//                        }
//                        ps2.executeBatch();
//                        con.commit();
//                    }
//                }
//            }
//        }
//        catch (SQLException e) {
//            if (con != null) {
//                try {
//                    con.rollback();
//                    throw new MyException("Sorry document wasn't saved");
//                }
//                catch (SQLException ex) {
//                    logger.error("Failed to rollback the transaction");
//                }
//            }
//            else{
//                logger.error("Connection Failed: {}", e.getMessage());
//                throw new MyException("Connection Failed");
//            }
//        }
//        finally {
//            if (con != null) {
//                try {
//                    con.close();
//                }
//                catch (SQLException ex) {
//                    logger.error("Failed to close the connection: {}", ex.getMessage());
//                }
//            }
//        }
//    }

    public int insertDocument(Connection con, Document document, String reg) throws MyException, SQLException {
        String documentCreation = "insert into documents (name, reg, boxId) values (?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(documentCreation, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, document.getName());
            ps.setString(2, reg);
            ps.setInt(3, document.getBoxId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (!rs.next()) {
                    return -1;
                }
                return rs.getInt(1);
            }
            catch (SQLException e) {
                logger.error("Failed to observe document id due to: {}", e.getMessage());
                return -1;
            }
        }
        catch (SQLException e) {
            System.out.println("document repository " + e.getMessage());
            e.printStackTrace();
            logger.error("Failed to create a document due to: {}", e.getMessage());
            throw e;
        }
    }
}
