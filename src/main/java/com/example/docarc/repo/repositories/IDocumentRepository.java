package com.example.docarc.repo.repositories;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IDocumentRepository {

    List<Document> getDocumentsByBoxIds(List<Box> boxes) throws MyException;
}
