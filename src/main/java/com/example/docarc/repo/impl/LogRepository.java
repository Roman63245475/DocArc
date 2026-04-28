package com.example.docarc.repo.impl;

import com.example.docarc.bll.LogService;
import com.example.docarc.repo.ConnectionManager;
import com.example.docarc.repo.repositories.ILogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        Connection con = null;
        try {
            con = ds.getConnection();
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
        }
        catch (Exception e){
            if (con != null) {
                try {
                    con.rollback();
                    logger.error("failed to save logs due to: {}", e.getMessage());
                    return false;
                } catch (SQLException ex) {
                    logger.error("Failed to rollback transaction: {}", ex.getMessage());
                    return false;
                }
            }
            logger.error("Failed to logs {}", e.getMessage());
            return false;
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    logger.error("Failed to close connection: {}", ex.getMessage());
                }
            }
        }
    }

    @Override
    public boolean saveErrorLogs(List<String> logs) {
        Connection con = null;
        try {
            con = ds.getConnection();
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
        }
        catch (Exception e){
            if (con != null) {
                try {
                    con.rollback();
                    logger.error("failed to save logs due to: {}", e.getMessage());
                    return false;
                } catch (SQLException ex) {
                    logger.error("Failed to rollback transaction: {}", ex.getMessage());
                    return false;
                }
            }
            logger.error("Failed to logs {}", e.getMessage());
            return false;
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    logger.error("Failed to close connection: {}", ex.getMessage());
                }
            }
        }
    }

}
