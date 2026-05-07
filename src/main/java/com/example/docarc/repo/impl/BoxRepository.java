package com.example.docarc.repo.impl;

import com.example.docarc.be.Box;
import com.example.docarc.be.User;
import com.example.docarc.custom_exceptions.MyException;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.IBoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoxRepository implements IBoxRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(BoxRepository.class);

    public BoxRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    @Override
    public void createBox(String boxName, User responsibleUser) {
        try (Connection con = ds.getConnection()) {
            String sqlPrompt = "Insert into boxes (name) values (?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, boxName);
            ps.execute();
            logger.info("User {} created box {}", responsibleUser.getUsername(), boxName);
        }
        catch (SQLException e) {
            logger.error("Failed to create a box due to: {}", e.getMessage());
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
                int boxId = rs.getInt("box_id");
                String boxName = rs.getString("box_name");
                userBoxes.add(new Box(boxId, boxName, user));
            }
            return userBoxes;
        }
        catch (SQLException e) {
            logger.error("Failed to observe user's boxes dut to: {}", e.getMessage());
            throw new MyException(e.getMessage());
        }
    }
}
