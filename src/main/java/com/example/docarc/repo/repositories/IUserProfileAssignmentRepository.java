package com.example.docarc.repo.repositories;

import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IUserProfileAssignmentRepository {

    List<User> findUsersEligibleForProfile(int profileId) throws DataBaseConnectionException, MyException;

    void assignProfileToUser(int profileId, int userId) throws DataBaseConnectionException, MyException, DuplicateException;
}
