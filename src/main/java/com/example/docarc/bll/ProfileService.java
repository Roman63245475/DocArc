package com.example.docarc.bll;

import com.example.docarc.be.Profile;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.ProfileRepository;
import com.example.docarc.repo.repositories.IProfileRepository;

import java.util.List;

public class ProfileService {
    IProfileRepository profileRepository = new ProfileRepository();


    public void createProfile(String name, double rotatation, double contrast, double brightness, Boolean greyscale) throws MyException, DuplicateException {
        checkName(name);
        profileRepository.addProfile(new Profile(name, rotatation, contrast, brightness, greyscale));
    }

    public List<Profile> getProfiles(){
        return profileRepository.getProfiles();
    }

    private void checkName(String name) throws MyException {
        if (name.isEmpty()) {
            throw new MyException("Name field can't be empty");
        }
    }
}