package com.example.docarc.bll;

import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.repositories.IBoxRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceTest {

    private IBoxRepository boxRepo;
    private DataService dataService;

    @BeforeEach
    public void setUp(){
        boxRepo = Mockito.mock(IBoxRepository.class);
        dataService = new DataService(boxRepo);
    }

    @AfterEach
    void success(TestInfo testInfo){
        System.out.println("Finished executing: " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Everything correct for box")
    void createBox_Correct() throws MyException, DuplicateException {
        String boxName = "My box";
        User user = new User(1, "user_test", "strong_pass", 1, true);
        Profile profile = new Profile(1, "grayscale_prof");

        dataService.createBox(boxName, profile, user);

        Mockito.verify(boxRepo).createBox(boxName, user, profile.getId());
    }

    @Test
    @DisplayName("Empty name for box")
    void createBox_EmptyName() {
        String boxName = "";
        User user = new User(1, "user_test", "strong_pass", 1, true);
        Profile profile = new Profile(1, "grayscale_prof");

        MyException exception = assertThrows(MyException.class, () -> {
            dataService.createBox(boxName, profile, user);
        });

        assertEquals("Name should not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Long name for box")
    void createBox_LongestName() {
        String boxName = "The highly fragile, indestructible, and completely empty box of heavy kalivan";
        User user = new User(1, "user_test", "strong_pass", 1, true);
        Profile profile = new Profile(1, "grayscale_prof");

        MyException exception = assertThrows(MyException.class, () -> {
            dataService.createBox(boxName, profile, user);
        });
        assertEquals("Name should be less than 50", exception.getMessage());
    }

    @Test
    @DisplayName("Null Profile for box")
    void createBox_NullProfile() {
        String boxName = "Really nice shaped box";
        User user = new User(1, "user_test", "strong_pass", 1, true);
        Profile profile = null;

        MyException exception = assertThrows(MyException.class, () -> {
            dataService.createBox(boxName, profile, user);
        });
        assertEquals("Please select a profile", exception.getMessage());
    }
}