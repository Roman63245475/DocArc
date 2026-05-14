package com.example.docarc.repo.repositories;

import com.example.docarc.be.Profile;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;

import java.util.List;

public interface IProfileRepository {
    void addProfile(Profile profile) throws DuplicateException, MyException;
    void updateProfile(Profile profile);
    void deleteProfile(Profile profile);
    List<Profile> getProfiles();
    List<Profile> getProfilesByUserId(int userId);
    List<Profile> getProfilesByClientId(int clientId) throws MyException;
}
