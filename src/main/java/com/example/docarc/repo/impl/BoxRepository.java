package com.example.docarc.repo.impl;

import com.example.docarc.be.Box;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.DuplicateException;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IBoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoxRepository implements IBoxRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(BoxRepository.class);
    private static final String sqlCreateBox = "Insert into boxes (name, user_id, profile_id) values (?, ?, ?)";

    public BoxRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    @Override
    public void createBox(String boxName, User responsibleUser, Integer profileId) throws DuplicateException {
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(sqlCreateBox)) {
            ps.setString(1, boxName);
            ps.setInt(2, responsibleUser.getId());
            if (profileId == null) {
                ps.setNull(3, Types.INTEGER);
            }
            else{
                ps.setInt(3, profileId);
            }
            ps.execute();
            logger.info("User {} created box {}", responsibleUser.getUsername(), boxName);
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                logger.warn("Attempt to insert crate a box with already existing name");
                throw new DuplicateException("Box with the same name already exists");
            }
            else{
                logger.error("Failed to create a box due to: {}", e.getMessage());
            }
        }
    }


    @Override
    public void deleteBox(int id) {
        System.out.println("Deleting box");
    }

    @Override
    public void renameBox(int boxId, String name) {
        System.out.println("renaming box");
    }

    @Override
    public List<Box> getUserBoxes(User user) throws MyException {
        List<Box> userBoxes = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "select * from boxes where user_id = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            logger.info("Boxes successfully observed for user {}", user.getUsername());
            while (rs.next()){
                int boxId = rs.getInt("id");
                String boxName = rs.getString("name");
                userBoxes.add(new Box(boxId, boxName, user, null));
            }
            return userBoxes;
        }
        catch (SQLException e) {
            logger.error("Failed to observe user's boxes dut to: {}", e.getMessage());
            throw new MyException(e.getMessage());
        }
    }
}
