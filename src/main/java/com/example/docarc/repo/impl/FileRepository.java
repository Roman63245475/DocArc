package com.example.docarc.repo.impl;

import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IFileRepository;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileRepository implements IFileRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(FileRepository.class);
    private static final String sqlGetFilesByDocument = "select * from files where documentId = ?";

    public FileRepository(){
        this.ds = ConnectionManager.getDataSource();
    }


//    @Override
//    public List<Tiff> getFilesByDocumentsIds(List<Document> documents) throws MyException {
//        System.out.println("aga nu");
//        return List.of();
//        if (documents.isEmpty()){
//            return List.of();
//        }
//        List<Tiff> files = new ArrayList<>();
//        String placeholder = documents.stream().map(doc -> "?").collect(Collectors.joining(","));
//        try (Connection con = ds.getConnection()) {
//            String sqlPrompt = "select * from files where documentId in (" + placeholder + ")";
//            PreparedStatement ps = con.prepareStatement(sqlPrompt);
//            for (int i = 0; i < documents.size(); i++){
//                ps.setInt(i+1, documents.get(i).getId());
//            }
//            ResultSet rs = ps.executeQuery();
//            logger.info("Files successfully observed");
//            while (rs.next()){
//                int file_id = rs.getInt("id");
//                String file_name = rs.getString("name");
//                int document_id = rs.getInt("documentId");
//                int order_id = rs.getInt("orderId");
//                byte[] file_content = rs.getBytes("file_content");
//                files.add(new Tiff(file_id, file_name, document_id, order_id, file_content));
//            }
//            return files;
//        }
//        catch (SQLException e) {
//            System.out.println("file repository " + e.getMessage());
//            e.printStackTrace();
//            logger.error("Failed to observe documents due to: {}", e.getMessage());
//            throw new MyException(e.getMessage());
//        }
//    }

    @Override
    public void saveFiles(int documentId, List<Tiff> files) throws MyException, SQLException, DataBaseConnectionException {
        String sqlPrompt = "insert into files (documentId, name, reference_id, file_content, order_id) values (?,?,?,?,?)";
        Connection con = null;
        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement ps_delete = con.prepareStatement("delete from files where documentId = ?"); PreparedStatement ps_save = con.prepareStatement(sqlPrompt)){
                ps_delete.setInt(1, documentId);
                ps_delete.executeUpdate();
                for (Tiff file : files) {
                    ps_save.setInt(1, documentId);
                    ps_save.setString(2, file.getFileName());
                    ps_save.setInt(3, file.getReference_id());
                    ps_save.setBytes(4, file.getFileContent());
                    ps_save.setInt(5, file.getOrderId());
                    ps_save.addBatch();
                }
                ps_save.executeBatch();
                con.commit();
            }
        }
        catch (SQLException e) {
            if (e instanceof SQLServerException){
                try {
                    con.rollback();
                }
                catch (SQLException e1) {
                    logger.error("Failed to rollback the transaction", e1);
                    throw new DataBaseConnectionException("Connection Failed");
                }
            }
            throw e;
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (SQLException e1) {
                    logger.error("Failed to close the connection", e1);
                }
            }
        }
    }

    @Override
    public List<Tiff> getFilesByDocumentId(int documentId) throws MyException {
        List<Tiff> files = new ArrayList<>();
        try (Connection con = ds.getConnection() ;PreparedStatement ps = con.prepareStatement(sqlGetFilesByDocument)){
            ps.setInt(1, documentId);
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next()){
                    int file_id = rs.getInt("id");
                    String file_name = rs.getString("name");
                    int document_id = rs.getInt("documentId");
                    int reference_id = rs.getInt("reference_id");
                    byte[] file_content = rs.getBytes("file_content");
                    int orderId = rs.getInt("order_id");
                    files.add(new Tiff(file_id, file_name, document_id, reference_id, file_content, orderId));
                }
                return files;
            }
        }
        catch (SQLException e) {
            System.out.println("file repository " + e.getMessage());
            e.printStackTrace();
            logger.error("Failed to save files due to: {}", e.getMessage());
            throw new MyException("soryan");
        }
    }
}
