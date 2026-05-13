package com.example.docarc.repo.impl;

import com.example.docarc.be.Client;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DataBaseConnectionException;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IClientProfileAssignmentRepository;
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

public class ClientProfileAssignmentRepository implements IClientProfileAssignmentRepository {

    /** Matches `users.isadmin` for regular (non-admin) accounts. */
    //private static final boolean ISADMIN_FOR_REGULAR_USER = false;

    private static final String SQL_FIND_ELIGIBLE_ClIENTS_FOR_PROFILE =
            "select c.* from clients c left join profile_client pc on c.id = pc.client_id and pc.profile_id = ? where pc.profile_id is null";
    //select c.*, (select count(u.id) from users u where u.clientId = c.id) as amountOfEmployee from clients c left join profile_client pc on c.id = pc.client_id and pc.profile_id = 24 where pc.profile_id is null
    private static final String SQL_ASSIGN_PROFILE_TO_CLIENT = "insert into profile_client (profile_id, client_id) values (?, ?)";

    private final DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(ClientProfileAssignmentRepository.class);

    public ClientProfileAssignmentRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    public ClientProfileAssignmentRepository(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<Client> findClientsEligibleForProfile(int profileId) throws DataBaseConnectionException, MyException {
        List<Client> clients = new ArrayList<>();
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ELIGIBLE_ClIENTS_FOR_PROFILE)) {
            ps.setInt(1, profileId);
            //ps.setBoolean(2, false);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    clients.add(new Client(id, name));
                }
            }
            return clients;
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
    public void assignProfileToClient(int profileId, int clientId) throws DataBaseConnectionException, MyException, DuplicateException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ASSIGN_PROFILE_TO_CLIENT)) {
            ps.setInt(1, profileId);
            ps.setInt(2, clientId);
            ps.executeUpdate();
        } catch (SQLServerException e) {
            throw new DataBaseConnectionException("Connection failed. " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                throw new DuplicateException("This client already has this profile.");
            }
            logger.error("assignProfileToUser failed: {}", e.getMessage());
            throw new MyException("Could not assign profile.");
        }
    }
}
