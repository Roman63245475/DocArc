package com.example.docarc.bll;

import com.example.docarc.be.Client;
import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.TestUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    @BeforeAll
    public static void setUpMethod(){
        userService = new UserService(new TestUserRepository());
    }


    @Test
    void getAllUsersByClient() throws MyException, DataBaseConnectionException {
        Client c = new Client(1, "Test Client");
        int forbiddenId = 1;
        List<ParentUser> users = userService.getAllUsersByClient(c, forbiddenId);
        boolean found = users.stream().anyMatch(u -> u.getId() == forbiddenId);
        assertFalse(found);
    }
}