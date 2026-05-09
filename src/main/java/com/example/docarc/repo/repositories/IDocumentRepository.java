package com.example.docarc.repo.repositories;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.custom_exceptions.MyException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDocumentRepository {

    List<Document> getDocumentsByBoxIds(List<Box> boxes) throws MyException;
    int insertDocument(Connection con, Document document, String reg) throws MyException, SQLException;
    //void saveDocument(Document document) throws MyException;
}
