package com.example.docarc.repo.repositories;

import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.MyException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IFileRepository {

    List<Tiff> getFilesByDocumentsIds(List<Document> documents) throws MyException;
    void saveFiles(Connection con, int documentId, List<Tiff> files) throws MyException, SQLException;
}
