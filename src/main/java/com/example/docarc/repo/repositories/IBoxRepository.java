package com.example.docarc.repo.repositories;

import com.example.docarc.be.User;

public interface IBoxRepository {

    void createBox(String boxName, User responsibleUser);
    void deleteBox(int id);
    void renameBox(int box_id, String name);
}
