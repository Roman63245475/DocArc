package com.example.docarc.repo.impl;

import com.example.docarc.be.Profile;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
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
    private static final String sqlUpdateProfile = "update profiles set name = ?, brightness = ?, contrast = ?, grayscale = ? where id = ?";
    public ProfileRepository(DataSource ds) {
        this.ds = ds;
    }

    public ProfileRepository() {
        this.ds = ConnectionManager.getDataSource();;
    }
    @Override
    public void addProfile(Profile profile) throws DuplicateException, MyException {
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "INSERT INTO profiles (name, brightness, contrast, grayscale, rotation) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, profile.getName());
            ps.setDouble(2, profile.getBrightness());
            ps.setDouble(3, profile.getContrast());
            ps.setBoolean(4, profile.getGrayscale());
            ps.setDouble(5, profile.getRotation());
            ps.executeUpdate();
            ps.close();
            logger.info("Profile named: {} has been created successfully", profile.getName());
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                logger.warn("Failed to create profile: Name '{}' already exists.", profile.getName());
                throw new DuplicateException("Profile with the same name already exists.");
            }
            else{
                logger.error("Failed to create a profile due to: {}", e.getMessage());
                throw new MyException("Could not create a profile, try again later.");
            }
        }
    }

    @Override
    public void updateProfile(Profile profile) throws DataBaseConnectionException {
        try(Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlUpdateProfile)) {
            ps.setString(1, profile.getName());
            ps.setDouble(2, profile.getBrightness());
            ps.setDouble(3, profile.getContrast());
            ps.setBoolean(4, profile.getGrayscale());
            ps.setInt(5, profile.getId());
            ps.executeUpdate();
            logger.info("Profile with id: {} has been updated successfully", profile.getId());
        }
        catch (SQLException e) {
            logger.error("Failed to update the profile due to: {}", e.getMessage());
            throw new DataBaseConnectionException("Failed to update the profile:\n" + e.getMessage());
        }
    }

    @Override
    public void deleteProfile(Integer id) {
        try(Connection con = ds.getConnection()){
            con.setAutoCommit(false);
            try{
                try(PreparedStatement ps = con.prepareStatement("DELETE FROM profiles WHERE id = ?")){
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                con.commit();
                logger.info("Profile with id: {} has been deleted successfully", id);
            } catch (SQLException e) {
                con.rollback();
                logger.error("Failed to delete the profile with id: {}", id);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Database connection failed while trying to delete a profile with id: {}. {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
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
            logger.info("Profiles successfully retrieved.");
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
        }
        catch (SQLException e) {
            logger.error("Failed to get profiles for user {} due to: {}.", userId, e.getMessage());
        }

        // Всегда добавляем Default профайл в начало списка
        Profile defaultProfile = new Profile(0, "Default", 0.0, 0.0, false);
        profiles.add(0, defaultProfile);
        logger.info("Successfully retrieved the profiles for user {}.", userId);
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
            logger.info("Successfully retrieved the profiles for client with id: {}.", clientId);
            return profiles;
        }
        catch (SQLException e) {
            logger.error("Failed to get profiles for client with id: {} due to: {}", clientId, e.getMessage());
            throw new MyException("Could not retrieve the profiles for client:\n" + e.getMessage());
        }
    }
}
