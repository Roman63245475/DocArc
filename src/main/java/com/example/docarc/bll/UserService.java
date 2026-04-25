package com.example.docarc.bll;

import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Role;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.UserRepository;
import com.example.docarc.repo.repositories.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class UserService {
    private IUserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService() {
        userRepository = new UserRepository();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }



    public List<ParentUser> getAllUsers(int id) throws DataBaseConnectionException, MyException {
        return this.userRepository.getAllUsers(id);
    }

    public void editUser(ParentUser user, String username, String password, Role role) throws MyException, DataBaseConnectionException, DuplicateException {
        if (checkUsername(username, password)){
            String hashedPassword = "";
            if (!password.isEmpty()){
                hashedPassword = passwordEncoder.encode(password);
            }
            else{
                hashedPassword = user.getPassword();
                if (passwordEncoder.matches(username, hashedPassword)){
                    throw new MyException("Username can't be equal to password");
                }
            }
            this.userRepository.editUser(user, username, hashedPassword, role == Role.ADMIN, user.getUsername().equals(username));
        }
    }

    private boolean checkUsername(String username, String password) throws MyException {
        if (username.length() < 8)
            throw new MyException("Username needs to have at least 8 characters");
        if (username.contains(" ")){
            throw new MyException("Username must not contain spaces");
        }
        if (username.equals(password)){
            throw new MyException("Username must not contain spaces");
        }
        return true;
    }

    private boolean checkPassword(String password) throws MyException {
        if (password.length() < 8)
            throw new MyException("Password needs to have at least 8 characters");
        if (password.contains(" ")){
            throw new MyException("Password must not contain spaces");
        }
        return true;
    }

    public void deleteUser(int id) throws MyException, DataBaseConnectionException {
        this.userRepository.deleteUser(id);
    }
}
