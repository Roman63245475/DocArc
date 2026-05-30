package com.example.docarc.repo.repositories;

import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.MyException;

import java.sql.SQLException;
import java.util.List;

public interface IFileRepository {

    void saveFiles(int documentId, List<Tiff> files, String username) throws MyException, SQLException, DataBaseConnectionException;
    List<Tiff> getFilesByDocumentId(int documentId) throws MyException;
}
