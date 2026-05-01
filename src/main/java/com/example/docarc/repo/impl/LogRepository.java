package com.example.docarc.repo.impl;

import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.ILogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogRepository implements ILogRepository {

    private DataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(LogRepository.class);
    public LogRepository(DataSource ds){
        this.ds = ds;
    }
    public LogRepository() {
        this.ds = ConnectionManager.getDataSource();
    }

    @Override
    public boolean saveAppLogs(List<String> logs) {
        try (Connection con = ds.getConnection()) {
            try {
                con.setAutoCommit(false);
                String sqlPrompt = "insert into app_logs (log) values (?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPrompt)){
                    for (String log : logs) {
                        ps.setString(1, log);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                rollbackQuietly(con);
                logger.error("Failed to save app logs due to: {}", e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Failed to save app logs due to: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean saveErrorLogs(List<String> logs) {
        try (Connection con = ds.getConnection()) {
            try {
                con.setAutoCommit(false);
                String sqlPrompt = "insert into error_logs (error_log) values (?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPrompt)){
                    for (String error_log : logs) {
                        ps.setString(1, error_log);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                rollbackQuietly(con);
                logger.error("Failed to save error logs due to: {}", e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Failed to save error logs due to: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAppLogs() {
        List<String> appLogs = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            try {
                String sqlPrompt = "select * from app_logs";
                PreparedStatement ps = con.prepareStatement(sqlPrompt);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    appLogs.add(rs.getString("log"));
                }
                return appLogs;
            } catch (SQLException e) {
                logger.error("Failed to get app logs due to: {}", e.getMessage());
                return appLogs;
            }
        }
        catch (SQLException e) {
            logger.error("Failed to load app logs due to: {}", e.getMessage());
            return appLogs;
        }
    }

    @Override
    public List<String> getErrorLogs() {
        return List.of();
    }

    private void rollbackQuietly(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ex) {
            logger.error("Failed to rollback transaction: {}", ex.getMessage());
        }
    }

}
