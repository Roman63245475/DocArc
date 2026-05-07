package com.example.docarc.bll;

import com.example.docarc.be.Box;
import com.example.docarc.be.Document;
import com.example.docarc.be.Tiff;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.BoxRepository;
import com.example.docarc.repo.impl.DocumentRepository;
import com.example.docarc.repo.impl.FileRepository;
import com.example.docarc.repo.repositories.IBoxRepository;
import com.example.docarc.repo.repositories.IDocumentRepository;
import com.example.docarc.repo.repositories.IFileRepository;

import java.util.List;

public class DataService {

    private IBoxRepository boxRepository;
    private IDocumentRepository documentRepository;
    private IFileRepository fileRepository;

    public DataService() {
        boxRepository = new BoxRepository();
        documentRepository = new DocumentRepository();
        fileRepository = new FileRepository();
    }

    public DataService(IBoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }
    public void createBox(String name, User responsibleUser) throws MyException {
        checkName(name);
        this.boxRepository.createBox(name, responsibleUser);
    }

    private void checkName(String name) throws MyException {
        if(name.isEmpty()){
            throw new MyException("Name should not be empty");
        }
        if(name.length()>50){
            throw new MyException("Name should be less than 50");
        }
    }

    public List<Box> getUserBoxes(User user) throws MyException {
        List<Box> userBoxes = this.boxRepository.getUserBoxes(user);
        List<Document> documents = this.documentRepository.getDocumentsByBoxIds(userBoxes);
        //List<Tiff> files =
        return null;
    }
}
