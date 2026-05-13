package com.example.docarc.repo.repositories;

import com.example.docarc.be.Client;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IClientProfileAssignmentRepository {

    List<Client> findClientsEligibleForProfile(int profileId) throws DataBaseConnectionException, MyException;

    void assignProfileToClient(int profileId, int clientId) throws DataBaseConnectionException, MyException, DuplicateException;
}
