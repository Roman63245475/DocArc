package com.example.docarc.bll;

import com.example.docarc.be.ParentUser;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.TestUserRepository;
import com.example.docarc.repo.repositories.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthService {
    private BCryptPasswordEncoder passwordEncoder; //kalivan -> $2ajnkse;rkljp7287346
    private IUserRepository userRepository;

    public AuthService(IUserRepository userRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthService(){
        // put here real repository, not the one I use as mock
        this.userRepository = new TestUserRepository();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ParentUser login(String username, String password) throws MyException {
        ParentUser usr = null;
        try {
            usr = userRepository.findUser(username);
            if (passwordEncoder.matches(password, usr.getPassword())){
                return usr;
            }
            throw new LoginException("Username os password is incorrect");
        }
        catch (DataBaseConnectionException | LoginException ex) {
            if (ex instanceof DataBaseConnectionException) {
                System.out.println("do some job");
            } else {
                System.out.println("do some other job");
            }
            throw new MyException(ex.getMessage());
        }
//        System.out.println(passwordEncoder.encode("kalivanskiy_password"));
//        return null;
    }
}

//input_value
// random salt generates -> (14)
//some_algorithm(input_value, salt) -> "2a$jhfkshluauhsiu97w3y8yf89e0h97w8geyfct"
//save to db "2a$jhfkshluauhsiu97w3y814yf89e0h97w8geyfct"
//input_value2 = input_value
//mathes (input_value2, input_value)
//searches for salt in already hashed password which is input_value
// hashes input_value2 basing on salt which we've got from input_value
// compares hashed input_value2 with hashed input_value response either true or false