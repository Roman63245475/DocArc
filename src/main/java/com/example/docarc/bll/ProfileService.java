package com.example.docarc.bll;

import com.example.docarc.be.Client;
import com.example.docarc.be.Profile;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.impl.ProfileRepository;
import com.example.docarc.repo.impl.ClientProfileAssignmentRepository;
import com.example.docarc.repo.repositories.IProfileRepository;
import com.example.docarc.repo.repositories.IClientProfileAssignmentRepository;

import java.util.List;

public class ProfileService {
    private final IProfileRepository profileRepository;
    private final IClientProfileAssignmentRepository profileAssignmentRepository;

    public ProfileService() {
        this.profileRepository = new ProfileRepository();
        this.profileAssignmentRepository = new ClientProfileAssignmentRepository();
    }

    public ProfileService(IProfileRepository profileRepository, IClientProfileAssignmentRepository profileAssignmentRepository) {
        this.profileRepository = profileRepository;
        this.profileAssignmentRepository = profileAssignmentRepository;
    }

    public void createProfile(String name, double contrast, double brightness, Boolean greyscale) throws MyException, DuplicateException {
        checkName(name);
        profileRepository.addProfile(new Profile(name, brightness, contrast, greyscale));
    }

    public List<Profile> getProfiles(){
        return profileRepository.getProfiles();
    }

    public List<Client> getClientsEligibleForProfileAssignment(int profileId) throws DataBaseConnectionException, MyException {
        return profileAssignmentRepository.findClientsEligibleForProfile(profileId);
    }

    public void assignProfileToClient(int profileId, int clientId) throws DataBaseConnectionException, MyException, DuplicateException {
        profileAssignmentRepository.assignProfileToClient(profileId, clientId);
    }

    private void checkName(String name) throws MyException {
        if (name.isEmpty()) {
            throw new MyException("Name field can't be empty");
        }
    }
}