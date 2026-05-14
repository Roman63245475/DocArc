package com.example.docarc.repo.impl;

import com.example.docarc.be.Profile;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProfileRepository implements IProfileRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(ProfileRepository.class);
    private static final String sqlGetProfilesByClient = "select p.name, p.id from profiles p left join profile_client on p.id = profile_client.profile_id where client_id = ?";
    public ProfileRepository(DataSource ds) {
        this.ds = ds;
    }

    public ProfileRepository() {
        this.ds = ConnectionManager.getDataSource();;
    }
    @Override
    public void addProfile(Profile profile) throws DuplicateException, MyException {
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "INSERT INTO profiles (name, brightness, contrast, grayscale) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, profile.getName());
            ps.setDouble(2, profile.getBrightness());
            ps.setDouble(3, profile.getContrast());
            ps.setBoolean(4, profile.getGrayscale());
            ps.executeUpdate();
            ps.close();
            logger.info("Profile added successfully");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                logger.warn("Attempt to insert a duplicate");
                throw new DuplicateException("Profile with the same name already exists");
            }
            else{
                logger.error("Failed to create a profile due to: {}", e.getMessage());
                throw new MyException("Sorry something went wrong went creating a profile");
            }

        }

    }

    @Override
    public void updateProfile(Profile profile) {
        System.out.println("I'm empty");
    }

    @Override
    public void deleteProfile(Profile profile) {
        System.out.println("I'm empty");
    }

    @Override
    public List<Profile> getProfiles() {
        String sqlPrompt = "Select * from profiles";
        List<Profile> profiles = new ArrayList<>();
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlPrompt)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double brightness = rs.getDouble("brightness");
                double contrast = rs.getDouble("contrast");
                boolean grayscale = rs.getBoolean("grayscale");
                profiles.add(new Profile(id,  name, brightness, contrast, grayscale));
            }
            logger.info("Profiles successfully selected");
            return profiles;
        }
        catch (SQLException e) {
            logger.error("Failed to get profiles due to: {}", e.getMessage());
            return List.of();
        }
    }




    @Override
    public List<Profile> getProfilesByUserId(int userId) {
        String sqlPrompt = "SELECT p.* FROM profiles p " +
                "INNER JOIN profile_user pu ON p.id = pu.profileId " +
                "WHERE pu.userId = ?";
        List<Profile> profiles = new ArrayList<>();
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlPrompt)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double brightness = rs.getDouble("brightness");
                double contrast = rs.getDouble("contrast");
                boolean grayscale = rs.getBoolean("grayscale");
                profiles.add(new Profile(id,  name, brightness, contrast, grayscale));
            }
            logger.info("Profiles for user {} successfully selected", userId);
        }
        catch (SQLException e) {
            logger.error("Failed to get profiles for user {} due to: {}", userId, e.getMessage());
        }

        // Всегда добавляем Default профайл в начало списка
        Profile defaultProfile = new Profile(0, "Default", 0.0, 0.0, false);
        profiles.add(0, defaultProfile);

        return profiles;
    }

    @Override
    public List<Profile> getProfilesByClientId(int clientId) throws MyException {
        List<Profile> profiles = new ArrayList<>();
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlGetProfilesByClient)){
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                profiles.add(new Profile(id, name));
            }
            Profile defaultProfile = new Profile("Default");
            profiles.add(defaultProfile);
            logger.info("Profiles for client {} successfully selected", clientId);
            return profiles;
        }
        catch (SQLException e) {
            logger.error("Failed to get profiles for client {} due to: {}", clientId, e.getMessage());
            throw new MyException("Something went wrong when observing client's profiles");
        }
    }
}
