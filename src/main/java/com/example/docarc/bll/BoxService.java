package com.example.docarc.bll;

import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.BoxRepository;
import com.example.docarc.repo.repositories.IBoxRepository;

public class BoxService {

    private IBoxRepository boxRepository;

    public BoxService() {
        boxRepository = new BoxRepository();
    }

    public BoxService(IBoxRepository boxRepository) {
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

}
