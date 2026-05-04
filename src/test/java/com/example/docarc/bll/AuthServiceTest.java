package com.example.docarc.bll;

import com.example.docarc.be.Admin;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.TestUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static AuthService authService;
    @BeforeAll
    public static void setUpMethod(){
        authService = new AuthService(new TestUserRepository());
    }

    @Test
    @DisplayName("login test")
    void login() throws MyException {
        Admin admin = new Admin(1, "roman_admin", "$2a$10$J3Ioaufs7oOjCP4iTMRQ6.YZvw7c24qFOL/CVN52sID.1.Kiy99kC");
        User user = new User(2, "kalivan_user", "$2a$10$UY5KhyIt6Jvr7mVEkbb9NOis.Ug/ULfKeMB8m3TWWmO4gcnAb6uYW", "hey");
        assertEquals(admin, authService.login("roman_admin", "yumma4444"));
        assertEquals(user, authService.login("kalivan_user", "kalivanskiy_password"));
        assertThrows(MyException.class, () -> authService.login("kalivan_usr", "kalivanskiy"));
    }


}