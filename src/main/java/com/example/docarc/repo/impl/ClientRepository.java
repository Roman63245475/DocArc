package com.example.docarc.repo.impl;

import com.example.docarc.be.Admin;
import com.example.docarc.be.Client;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements IClientRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClientRepository.class);
    private static final String sqlCreateClient = "Insert into clients(name, country, city) values(?, ?, ?)";
    private static final String sqlGetClients = "select *, (select COUNT(users.id) from users where clientId = clients.id) as amount_of_employees from clients";
    private DataSource ds;

    public ClientRepository(){
        this.ds = ConnectionManager.getDataSource();
    }
    @Override
    public void createClient(Client client, Admin responsibleAdmin) throws MyException {
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlCreateClient)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getCountry());
            ps.setString(3, client.getCity());
            ps.execute();
            logger.info("User {} created client {}", responsibleAdmin.getUsername(), client.getName());
        }
        catch (SQLException e) {
            logger.error("Failed to create a client due to: {}", e.getMessage());
            throw new MyException("Sorry client was not created");
        }
    }

    @Override
    public List<Client> getClients() throws MyException {
        List<Client> clients = new ArrayList<>();
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlGetClients)) {
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String country = rs.getString("country");
                    String city = rs.getString("city");
                    int amountOfEmployees = rs.getInt("amount_of_employees");
                    clients.add(new Client(id, name, country, city, amountOfEmployees));
                }
            }
            logger.info("clients successfully observed");
            return clients;
        }
        catch (SQLException e) {
            logger.error("Failed to observe clients due to: {}", e.getMessage());
            throw new MyException("Sorry client was not created");
        }
    }
}
