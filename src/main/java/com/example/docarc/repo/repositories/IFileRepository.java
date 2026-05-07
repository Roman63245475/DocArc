package com.example.docarc.repo.repositories;

import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IFileRepository {

    List<Tiff> getFilesByDocumentsIds(List<Document> documents) throws MyException;
}
