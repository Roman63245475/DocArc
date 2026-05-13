package com.example.docarc.repo.impl;

import com.example.docarc.be.Admin;
import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.repositories.IUserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestUserRepository implements IUserRepository {
    private List<HashMap<String, String>> userData;
    private List<String> names = new ArrayList<>(Arrays.asList("Sofia_user", "Tove_user", "Zlata_user", "Dasha_user"));

    public TestUserRepository(){
        this.userData = new ArrayList<>();
        initializeUsers();
    }
    @Override
    public ParentUser findUser(String username) throws LoginException {
        for (HashMap<String, String> h : this.userData) {
            if (h.get("username").equals(username)) {
                try {
                    int id = Integer.parseInt(h.get("id"));
                    String us_name = h.get("username");
                    String password = h.get("password");
                    ParentUser parentUser = Role.valueOf(h.get("role")) == Role.ADMIN ? new Admin(id, username, password) : new User(id, username, password, "hey");
                    return parentUser;
                } catch (IllegalArgumentException ex) {
                    System.out.println("developer screwed up");
                }
            }
        }
        throw new LoginException("User not found");
    }

    @Override
    public void createUser(String username, String password, boolean isAdmin) throws DataBaseConnectionException, LoginException, DuplicateException, MyException {
        System.out.println("chetko");

    }

    @Override
    public List<ParentUser> getAllUsersByClient(int clientId, int id) {
        return List.of();
    }

    //@Override
    public List<ParentUser> getAllUsers(int id) throws DataBaseConnectionException, MyException {
        List<ParentUser> users = new ArrayList<>();
        for (HashMap<String, String> h : this.userData) {
            try {
                int user_id = Integer.parseInt(h.get("id"));
                if (user_id == id) {
                    continue;
                }
                String us_name = h.get("username");
                String password = h.get("password");
                ParentUser parentUser = Role.valueOf(h.get("role")) == Role.ADMIN ? new Admin(user_id, us_name, password) : new User(user_id, us_name, password, "hey");
                users.add(parentUser);
            }
            catch (NumberFormatException ex) {
                System.out.println("developer screwed up, even when making tests");
            }
        }
        return users;
    }

    @Override
    public void editUser(ParentUser user, String username, String password, boolean isAdmin, boolean sameUsername) {
        System.out.println("aga nu");
    }

    @Override
    public void deleteUser(int id) throws DataBaseConnectionException, MyException {
        System.out.println("aga nu zaebok");
    }

    private void initializeUsers() {
        //good to have these hardcoded users
        HashMap<String, String> admin = new HashMap<>();
        admin.put("id", "1");
        admin.put("username", "roman_admin");
        admin.put("password", "$2a$10$J3Ioaufs7oOjCP4iTMRQ6.YZvw7c24qFOL/CVN52sID.1.Kiy99kC"); //yumma4444
        admin.put("role", "ADMIN");
        HashMap<String, String> user = new HashMap<>();
        user.put("id", "2");
        user.put("username", "kalivan_user");
        user.put("password", "$2a$10$UY5KhyIt6Jvr7mVEkbb9NOis.Ug/ULfKeMB8m3TWWmO4gcnAb6uYW"); //kalivanskiy_password
        user.put("role", "USER");
        userData.add(admin);
        userData.add(user);
        for (int i = 0; i < this.names.size(); i++) {
            int minId = 3;
            HashMap<String, String> h = new HashMap<>();
            h.put("id", String.valueOf(minId + i));
            h.put("username", this.names.get(i));
            h.put("password", this.names.get(i) + "_" + (minId + i));
            String role = ((minId + i) % 2 == 0) ? "ADMIN" : "USER";
            h.put("role", role);
            userData.add(h);
        }
    }
}


//map
// key -> value
// 2 -> privet
//map.get()
// "2" (str) -> int 2
// "s2" (str) -> no way
