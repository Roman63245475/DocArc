package com.example.docarc.repo.repositories;

import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.LoginException;

public interface IUserRepository {

    ParentUser findUser(String username) throws DataBaseConnectionException, LoginException;
}
