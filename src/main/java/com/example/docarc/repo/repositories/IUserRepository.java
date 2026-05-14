package com.example.docarc.repo.repositories;

import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IUserRepository {

    ParentUser findUser(String username) throws DataBaseConnectionException, LoginException;

    void createUser(String username, String password, boolean isAdmin, int clientId) throws DataBaseConnectionException, LoginException, DuplicateException, MyException;

    List<ParentUser> getAllUsersByClient(int clientId, int id) throws DataBaseConnectionException, MyException;

    void editUser(ParentUser user, String username, String password, boolean isAdmin, boolean sameUsername) throws DataBaseConnectionException, MyException, DuplicateException;

    void deleteUser(int id) throws DataBaseConnectionException, MyException;
}
