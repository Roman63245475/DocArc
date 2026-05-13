package com.example.docarc.repo.repositories;

import com.example.docarc.be.Admin;
import com.example.docarc.be.Client;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IClientRepository {
    void createClient(Client client, Admin responsibleAdmin) throws MyException;
    List<Client> getClients() throws MyException;
}
