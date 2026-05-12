package com.example.docarc.bll;

import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.ProfileRepository;
import com.example.docarc.repo.impl.UserProfileAssignmentRepository;
import com.example.docarc.repo.repositories.IProfileRepository;
import com.example.docarc.repo.repositories.IUserProfileAssignmentRepository;

import java.util.List;

public class ProfileService {
    private final IProfileRepository profileRepository;
    private final IUserProfileAssignmentRepository profileAssignmentRepository;

    public ProfileService() {
        this.profileRepository = new ProfileRepository();
        this.profileAssignmentRepository = new UserProfileAssignmentRepository();
    }

    public ProfileService(IProfileRepository profileRepository, IUserProfileAssignmentRepository profileAssignmentRepository) {
        this.profileRepository = profileRepository;
        this.profileAssignmentRepository = profileAssignmentRepository;
    }

    public void createProfile(String name, double rotatation, double contrast, double brightness, Boolean greyscale) throws MyException, DuplicateException {
        checkName(name);
        profileRepository.addProfile(new Profile(name, rotatation, contrast, brightness, greyscale));
    }

    public List<Profile> getProfiles(){
        return profileRepository.getProfiles();
    }

    public List<User> getUsersEligibleForProfileAssignment(int profileId) throws DataBaseConnectionException, MyException {
        return profileAssignmentRepository.findUsersEligibleForProfile(profileId);
    }

    public void assignProfileToUser(int profileId, int userId) throws DataBaseConnectionException, MyException, DuplicateException {
        profileAssignmentRepository.assignProfileToUser(profileId, userId);
    }

    private void checkName(String name) throws MyException {
        if (name.isEmpty()) {
            throw new MyException("Name field can't be empty");
        }
    }
}