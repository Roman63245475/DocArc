package com.example.docarc.repo.impl;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IDocumentRepository;
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
                int box_reference = rs.getInt("box_id");
                documents.add(new Document(document_id, document_name, box_reference));
            }
            return documents;
        }
        catch (SQLException e) {
            logger.error("Failed to observe documents due to: {}", e.getMessage());
            throw new MyException(e.getMessage());
        }
    }
}
