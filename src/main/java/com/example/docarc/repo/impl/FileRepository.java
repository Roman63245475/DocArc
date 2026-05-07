package com.example.docarc.repo.impl;

import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
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

    public FileRepository(){
        this.ds = ConnectionManager.getDataSource();
    }


    @Override
    public List<Tiff> getFilesByDocumentsIds(List<Document> documents) throws MyException {
        if (documents.isEmpty()){
            return List.of();
        }
        List<Tiff> files = new ArrayList<>();
        String placeholder = documents.stream().map(doc -> "?").collect(Collectors.joining(","));
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "select * from files where documentId in (" + placeholder + ")";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            for (int i = 0; i < documents.size(); i++){
                ps.setInt(i+1, documents.get(i).getId());
            }
            ResultSet rs = ps.executeQuery();
            logger.info("Files successfully observed");
            while (rs.next()){
                int file_id = rs.getInt("id");
                String file_name = rs.getString("name");
                int document_id = rs.getInt("box_id");
                int order_id = rs.getInt("orderId");
                String file_content = rs.getString("file_content");
                files.add(new Tiff(file_id, file_name, document_id, order_id, file_content));
            }
            return files;
        }
        catch (SQLException e) {
            logger.error("Failed to observe documents due to: {}", e.getMessage());
            throw new MyException(e.getMessage());
        }
    }
}
