package com.example.docarc.repo.impl;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IUserRepository;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {

    private ConnectionManager cm;
    private DataSource ds;
    private Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final String sqlGetUsersByClient = "select * from users where clientId = ? and id !=?";
    private static final String sqlFindUserByUsername = "select * from users where username = ?";
    private static final String sqlCreateUser = "Insert into users (username, password, isadmin, clientId, is_active) values (?, ?, ?, ?, ?)";

    public UserRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    @Override
    public ParentUser findUser(String username) throws DataBaseConnectionException, LoginException {
        try(Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlFindUserByUsername);){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isadmin");
                int clientId = rs.getInt("clientId");
                boolean active = rs.getBoolean("is_active");
                ParentUser user = (isAdmin) ? new Admin(id, username, password, active) : new User(id, username, password, clientId, active);
                logger.info("Successfully retrieved the user with id: {}.", id);
                return user;
            }
            //if user not found
            logger.warn("Failed to retrieve the user with id: {}.", username);
            throw new LoginException("User not found");
        } catch (SQLServerException e) {
            //if couldn't have gotten a connection obj
            logger.error("Database connection failed while trying to get the user by username");
            throw new DataBaseConnectionException("Connection failed");
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            //if dev is an idiot
            throw new DataBaseConnectionException("Sorry something went wrong");
        }
    }

    public void createUser(String username, String password, boolean isAdmin, int clientId, boolean isActive) throws DataBaseConnectionException, DuplicateException, MyException {
        Connection con = null;
        try {
            con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sqlCreateUser);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setBoolean(3, isAdmin);
            ps.setInt(4, clientId);
            ps.setBoolean(5, isActive);
            ps.executeUpdate();
            logger.info("Successfully created the user with username: {}.", username);
        }
        catch (SQLException e){
            if (con == null){
                logger.error("Database connection failed while trying to create the user with username: {}.", username);
                throw new DataBaseConnectionException("Connection failed");
            }
            else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                logger.warn("Failed to create a user, user with the username {} already exists.", username);
                throw new DuplicateException("User with this username already exists");
            }
            else {
                throw new MyException("Sorry something went wrong");
            }
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.error("Failed to close connection due to {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public List<ParentUser> getAllUsersByClient(int clientId, int userId) throws DataBaseConnectionException, MyException {
        List<ParentUser> users = new ArrayList<>();
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlGetUsersByClient);) {
            ps.setInt(1, clientId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int Id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isadmin");
                boolean active = rs.getBoolean("is_active");
                ParentUser user = (isAdmin) ? new Admin(Id, username, password, active) : new User(Id, username, password, active);
                users.add(user);
            }
            logger.info("Successfully retrieved the users by client with id: {}.", clientId);
            return users;
        }
        catch (SQLServerException e){
            logger.error("Database connection failed while trying to get the users by client");
            throw new DataBaseConnectionException("Connection failed");
        }
        catch (SQLException e){
            throw new MyException("Sorry something went wrong");
        }
    }

    @Override
    public void editUser(ParentUser user, String username, String password, boolean isAdmin, boolean sameUsername, boolean isActive) throws DataBaseConnectionException, MyException, DuplicateException {
        Connection con = null;
        try {
            PreparedStatement ps = null;
            con = ds.getConnection();
            if (sameUsername){
                String sqlPrompt = "Update users set password = ?, isadmin = ?, is_active = ? where id = ?";
                ps = con.prepareStatement(sqlPrompt);
                ps.setString(1, password);
                ps.setBoolean(2, isAdmin);
                ps.setBoolean(3, isActive);
                ps.setInt(4, user.getId());
            }
            else{
                String sqlPrompt = "Update users set username = ?, password = ?, isadmin = ?, is_active = ? where id = ?";
                ps = con.prepareStatement(sqlPrompt);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setBoolean(3, isAdmin);
                ps.setBoolean(4, isActive);
                ps.setInt(5, user.getId());
            }
            ps.executeUpdate();
            logger.info("Successfully updated the user with id: {}.", user.getId());
        }
        catch (SQLException e){
            if (con == null){
                logger.error("Database connection failed while trying to update the user.");
                throw new DataBaseConnectionException("Connection failed");
            }
            else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                logger.warn("User with the same username already exists: {}.", username);
                throw new DuplicateException("User with this username already exists");
            }
            else {
                throw new MyException("Sorry something went wrong");
            }
        }
    }

    @Override
    public void deleteUser(int id) throws DataBaseConnectionException, MyException {
        try(Connection con = ds.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("delete from users where id = ?")){
                ps.setInt(1, id);
                ps.executeUpdate();
                logger.info("Successfully deleted the user with id: {}.", id);
            }
        } catch (SQLServerException e){
            logger.error("Connection failed due to: {}", e.getMessage());
            throw new DataBaseConnectionException("Connection failed");
        }
        catch (SQLException e){
            logger.error("SQL prompt failed");
            throw new MyException("Sorry something went wrong");
        }
    }

    //David you can implement your db queries here for the users table
}
