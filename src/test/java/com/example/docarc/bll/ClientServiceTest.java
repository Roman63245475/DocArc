package com.example.docarc.bll;

import com.example.docarc.be.Admin;
import com.example.docarc.be.Client;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.repositories.IClientRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {
    private IClientRepository clientRepo;
    private ClientService clientService;

    @BeforeEach
    void setUp(){
        clientRepo = Mockito.mock(IClientRepository.class);
        clientService = new ClientService(clientRepo);
    }

    @AfterEach
    void success(TestInfo testInfo){
        System.out.println("Finished executing: " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Empty fields on creation")
    void createClient_EmptyFields(){
        String name = " ";
        String country = "";
        String city = " ";
        Admin responsibleAdmin = new Admin(1, "user_admin", "strongest_pass", true);

        MyException exception = assertThrows(MyException.class, () -> {
            clientService.createClient(name, country, city, responsibleAdmin);
        });

        assertEquals("Make sure that all the fields are filled out", exception.getMessage());
    }

    @Test
    @DisplayName("Filled out fields on creation")
    void createClient_FieldsFilledOut() throws MyException {
        String name = "My amazing name";
        String country = "Denmark";
        String city = "Kolind";
        Admin responsibleAdmin = new Admin(1, "user_admin", "strongest_pass", true);
        clientService.createClient(name, country, city, responsibleAdmin);
        Mockito.verify(clientRepo).createClient(Mockito.any(Client.class), Mockito.any(Admin.class));
    }

}