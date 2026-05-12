package com.example.docarc.repo.impl;

import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IUserProfileAssignmentRepository;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserProfileAssignmentRepository implements IUserProfileAssignmentRepository {

    /** Matches `users.isadmin` for regular (non-admin) accounts. */
    //private static final boolean ISADMIN_FOR_REGULAR_USER = false;

    private static final String SQL_FIND_ELIGIBLE_USERS_FOR_PROFILE =
            "SELECT u.id, u.username, u.password, u.isadmin "
                    + "FROM users u "
                    + "LEFT JOIN profile_user pu ON pu.[userId] = u.id AND pu.[profileId] = ? "
                    + "WHERE u.isadmin = ? AND pu.[userId] IS NULL";

    private static final String SQL_ASSIGN_PROFILE_TO_USER =
            "INSERT INTO profile_user (profileId, userId) VALUES (?, ?)";

    private final DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileAssignmentRepository.class);

    public UserProfileAssignmentRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    public UserProfileAssignmentRepository(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<User> findUsersEligibleForProfile(int profileId) throws DataBaseConnectionException, MyException {
        List<User> users = new ArrayList<>();
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ELIGIBLE_USERS_FOR_PROFILE)) {
            ps.setInt(1, profileId);
            ps.setBoolean(2, false);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    users.add(new User(id, username, password));
                }
            }
            return users;
        } catch (SQLServerException e) {
            throw new DataBaseConnectionException("Connection failed. " + e.getMessage());
        } catch (SQLException e) {
            logger.error("findUsersEligibleForProfile failed: {}", e.getMessage());
            throw new MyException("Could not load users for assignment.");
        }
    }

//    private static User mapRowToUser(ResultSet rs) throws SQLException {
//        int id = rs.getInt("id");
//        String username = rs.getString("username");
//        String password = rs.getString("password");
//        return new User(id, username, password, "otherInfo");
//    }

    @Override
    public void assignProfileToUser(int profileId, int userId) throws DataBaseConnectionException, MyException, DuplicateException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ASSIGN_PROFILE_TO_USER)) {
            ps.setInt(1, profileId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLServerException e) {
            throw new DataBaseConnectionException("Connection failed. " + e.getMessage());
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                throw new DuplicateException("This user already has this profile.");
            }
            logger.error("assignProfileToUser failed: {}", e.getMessage());
            throw new MyException("Could not assign profile.");
        }
    }
}
