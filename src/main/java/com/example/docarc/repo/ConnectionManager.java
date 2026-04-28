package com.example.docarc.repo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Properties;

public class ConnectionManager {

    private static final HikariDataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);





    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/db_config.settings"));

            String url = "jdbc:sqlserver://"
                    + props.getProperty("server")
                    + ":"
                    + props.getProperty("port")
                    + ";databaseName="
                    + props.getProperty("database")
                    + ";encrypt="
                    + props.getProperty("encrypt")
                    + ";trustServerCertificate="
                    + props.getProperty("trustServerCertificate");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(props.getProperty("user"));
            config.setPassword(props.getProperty("password"));

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setLeakDetectionThreshold(2000);

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
