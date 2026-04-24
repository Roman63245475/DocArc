package com.example.docarc.bll;

import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.UserRepository;
import com.example.docarc.repo.repositories.IUserRepository;

import java.util.List;

public class UserService {
    private IUserRepository userRepository;

    public UserService() {
        userRepository = new UserRepository();
    }



    public List<ParentUser> getAllUsers(int id) throws DataBaseConnectionException, MyException {
        return this.userRepository.getAllUsers(id);
    }
}
