package com.example.docarc.repo.impl;

import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.repo.repositories.IUserRepository;

public class UserRepository implements IUserRepository {
    @Override
    public ParentUser findUser(String username) throws DataBaseConnectionException {
        throw new DataBaseConnectionException("Connection failed");
    }

    //David you can implement your db queries here for the users table
}
