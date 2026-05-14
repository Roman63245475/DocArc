package com.example.docarc.bll;

import com.example.docarc.be.*;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.BoxRepository;
import com.example.docarc.repo.impl.DocumentRepository;
import com.example.docarc.repo.impl.FileRepository;
import com.example.docarc.repo.repositories.IBoxRepository;
import com.example.docarc.repo.repositories.IDocumentRepository;
import com.example.docarc.repo.repositories.IFileRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void createBox(String name, Profile profile, User responsibleUser) throws MyException, DuplicateException {
        checkName(name);
        if (profile == null) {
            throw new MyException("Please select a profile");
        }
        this.boxRepository.createBox(name, responsibleUser, profile.getId());
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
        List<Tiff> files = this.fileRepository.getFilesByDocumentsIds(documents);
        Map<Integer, List<Tiff>> filesByDocsId = files.stream().collect(Collectors.groupingBy(Tiff::getDocumentId));
        relateData(documents, filesByDocsId);
        Map<Integer, List<Document>> documentsByBoxIds = documents.stream().collect(Collectors.groupingBy(Document::getBoxId));
        relateData(userBoxes, documentsByBoxIds);
        return userBoxes;
    }

    private <T extends Data> void relateData(List<? extends IDataSettable<T>> to, Map<Integer, List<T>> data){
        for (IDataSettable<T> obj : to){
            List<T> lst = data.getOrDefault(obj.getId(), List.of());
            obj.setData(lst);
        }
    }
}
