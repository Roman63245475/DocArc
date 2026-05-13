package com.example.docarc.bll;

import com.example.docarc.be.Admin;
import com.example.docarc.be.Client;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.ClientRepository;
import com.example.docarc.repo.repositories.IClientRepository;

import java.util.List;

public class ClientService {

    private IClientRepository clientRepository;

    public ClientService(){
        this.clientRepository = new ClientRepository();
    }

    public void createClient(String name, String country, String city, Admin responsibleAdmin) throws MyException {
        if (!name.isEmpty() && !country.isEmpty() && !city.isEmpty()){
            Client client = new Client(name, country, city);
            this.clientRepository.createClient(client, responsibleAdmin);
        }
        else {
            throw new MyException("Make sure that all the fields are filled out");
        }
    }

    public List<Client> getClients() throws MyException {
        return this.clientRepository.getClients();
    }
}
