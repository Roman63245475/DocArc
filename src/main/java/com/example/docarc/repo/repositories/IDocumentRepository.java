package com.example.docarc.repo.repositories;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.custom_exceptions.MyException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDocumentRepository {

    List<Document> getDocumentsByBoxIds(List<Box> boxes) throws MyException;

    void saveDocument(Document document) throws MyException;

    int insertDocument(Connection con, Document document) throws MyException, SQLException;

    void updateDocument(Document document);
    List<Document> getDocumentsByBox(int box_id, int exceptional_document_id) throws MyException;
    //void saveDocument(Document document) throws MyException;
}
