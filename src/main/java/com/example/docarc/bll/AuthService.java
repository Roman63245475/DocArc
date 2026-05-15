package com.example.docarc.bll;

import com.example.docarc.be.ParentUser;
import com.example.docarc.be.Profile;
import com.example.docarc.be.Role;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.LoginException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.ProfileRepository;
import com.example.docarc.repo.impl.UserRepository;
import com.example.docarc.repo.repositories.IProfileRepository;
import com.example.docarc.repo.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class AuthService {
    private BCryptPasswordEncoder passwordEncoder; //kalivan -> $2ajnkse;rkljp7287346
    private IUserRepository userRepository;
    private IProfileRepository profileRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(IUserRepository userRepository){
        this.userRepository = userRepository;
        this.profileRepository = new ProfileRepository();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthService(){
        // put here real repository, not the one I use as mock
        this.userRepository = new UserRepository();
        this.profileRepository = new ProfileRepository();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ParentUser login(String username, String password) throws MyException {
        ParentUser usr = null;
        try {
            usr = userRepository.findUser(username);
            if (passwordEncoder.matches(password, usr.getPassword())){
                logger.info("{} logged in successfully", username);

                // Если пользователь является экземпляром User, загружаем его профайлы
                if (usr instanceof User) {
                    if (((User) usr).isUserActive()){
                        User user = (User) usr;
                        List<Profile> userProfiles = profileRepository.getProfilesByUserId(user.getId());
                        user.setProfiles(userProfiles);
                        logger.info("Loaded profiles for user {}", username);
                    }else{
                        throw new LoginException("User is not active, please contact your administrator.");
                    }
                }

                return usr;
            }
            logger.warn("Login failed: invalid password for user={}",  username);
            throw new MyException("Username os password is incorrect");
        }
        catch (DataBaseConnectionException | LoginException ex) {
            if (ex instanceof DataBaseConnectionException) {
                logger.error("Login failed due to: {}", ex.getMessage());
            } else {
                logger.warn("Login failed: user not found (username={})", username);
            }
            throw new MyException(ex.getMessage());
        }
//        System.out.println(passwordEncoder.encode("kalivanskiy_password"));
//        return null;
    }

    private boolean checkUsername(String username, String password) throws MyException {
        if (username.length() < 8)
            throw new MyException("Username needs to have at least 8 characters");
        if (username.contains(" ")){
            throw new MyException("Username must not contain spaces");
        }
        if (username.equals(password)){
            throw new MyException("Username must not be equal to password");
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


    public void createUser(String userName, String password, Role role, int clientId, boolean isActive) throws MyException, DuplicateException, DataBaseConnectionException, LoginException {
        logger.info("ENTER createUser");
        if (checkUsername(userName, password) && checkPassword(password)){
            String hashedPassword = passwordEncoder.encode(password);
            this.userRepository.createUser(userName, hashedPassword, role == Role.ADMIN, clientId, isActive);
            logger.info("User '{}' created successfully", userName);
        }
        else{
            logger.warn("Failed to create user: invalid username or password");
        }
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
