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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {

    private ConnectionManager cm;

    public UserRepository() {
        try {
            this.cm = new ConnectionManager();
        } catch (IOException e) {
            System.out.println("kalivan");
        }
    }

    @Override
    public ParentUser findUser(String username) throws DataBaseConnectionException, LoginException {
        try(Connection con = cm.getConnection()){
            String sqlPrompt = "select * from users where username = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isadmin");
                ParentUser user = (isAdmin) ? new Admin(id, username, password) : new User(id, username, password, "otherInfo");
                return user;
            }
            throw new LoginException("User not found");
        } catch (SQLServerException e) {
            throw new DataBaseConnectionException("Connection failed. " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String username, String password, boolean isAdmin) throws DataBaseConnectionException, DuplicateException, MyException {
        Connection con = null;
        try {
            con = cm.getConnection();
            String sqlPrompt = "Insert into users (username, password, isadmin) values (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setBoolean(3, isAdmin);
            ps.executeUpdate();
        }
        catch (SQLException e){
            if (con == null){
                throw new DataBaseConnectionException("Connection failed");
            }
            else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                throw new DuplicateException("User with this username already exists");
            }
            else {
                throw new MyException("Sorry something went wrong");
            }
        }
    }

    @Override
    public List<ParentUser> getAllUsers(int id) throws DataBaseConnectionException, MyException {
        List<ParentUser> users = new ArrayList<>();
        try (Connection con = cm.getConnection()) {
            String sqlPrompt = "Select * from users where id != ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int Id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isadmin");
                ParentUser user = (isAdmin) ? new Admin(Id, username, password) : new User(Id, username, password, "otherInfo");
                users.add(user);
            }
            return users;
        }
        catch (SQLServerException e){
            throw new DataBaseConnectionException("Connection failed");
        }
        catch (SQLException e){
            throw new MyException("Sorry something went wrong");
        }
    }

    @Override
    public void editUser(ParentUser user, String username, String password, boolean isAdmin, boolean sameUsername) throws DataBaseConnectionException, MyException, DuplicateException {
        Connection con = null;
        try {
            PreparedStatement ps = null;
            con = cm.getConnection();
            if (sameUsername){
                con = cm.getConnection();
                String sqlPrompt = "Update users set password = ?, isadmin = ? where id = ?";
                ps = con.prepareStatement(sqlPrompt);
                ps.setString(1, password);
                ps.setBoolean(2, isAdmin);
                ps.setInt(3, user.getId());
            }
            else{
                String sqlPrompt = "Update users set username = ?, password = ?, isadmin = ? where id = ?";
                ps = con.prepareStatement(sqlPrompt);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setBoolean(3, isAdmin);
                ps.setInt(4, user.getId());
            }
            ps.executeUpdate();
        }
        catch (SQLException e){
            if (con == null){
                throw new DataBaseConnectionException("Connection failed");
            }
            else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                throw new DuplicateException("User with this username already exists");
            }
            else {
                throw new MyException("Sorry something went wrong");
            }
        }
    }

    @Override
    public void deleteUser(int id) throws DataBaseConnectionException, MyException {
        try(Connection con = cm.getConnection()){
            try(PreparedStatement ps = con.prepareStatement("delete from users where id = ?")){
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLServerException e){
            throw new DataBaseConnectionException("Connection failed");
        }
        catch (SQLException e){
            throw new MyException("Sorry something went wrong");
        }
    }

    //David you can implement your db queries here for the users table
}
