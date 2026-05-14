package com.example.docarc.repo.repositories;

import com.example.docarc.be.Box;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IBoxRepository {

    void createBox(String boxName, User responsibleUser,  Integer profileId) throws DuplicateException;
    void deleteBox(int id);
    void renameBox(int box_id, String name);
    List<Box> getUserBoxes(User user) throws MyException;
}
