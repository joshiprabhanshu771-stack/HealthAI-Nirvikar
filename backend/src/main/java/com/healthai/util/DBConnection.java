package com.healthai.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DBConnection provides JDBC database connections for MySQL.
 * Reads configurations from application.properties or environment variables.
 */
public class DBConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/healthai_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = "";
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static boolean driverLoaded = false;

    static {
        loadProperties();
        try {
            Class.forName(dbDriver);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private static void loadProperties() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                if (prop.getProperty("spring.datasource.url") != null) {
                    dbUrl = prop.getProperty("spring.datasource.url");
                } else if (prop.getProperty("db.url") != null) {
                    dbUrl = prop.getProperty("db.url");
                }
                if (prop.getProperty("spring.datasource.username") != null) {
                    dbUser = prop.getProperty("spring.datasource.username");
                } else if (prop.getProperty("db.username") != null) {
                    dbUser = prop.getProperty("db.username");
                }
                if (prop.getProperty("spring.datasource.password") != null) {
                    dbPassword = prop.getProperty("spring.datasource.password");
                } else if (prop.getProperty("db.password") != null) {
                    dbPassword = prop.getProperty("db.password");
                }
                if (prop.getProperty("spring.datasource.driver-class-name") != null) {
                    dbDriver = prop.getProperty("spring.datasource.driver-class-name");
                }
            }
        } catch (Exception ignored) {
        }

        String envUrl = System.getenv("HEALTHAI_DB_URL");
        if (envUrl != null && !envUrl.isBlank()) dbUrl = envUrl;
        String envUser = System.getenv("HEALTHAI_DB_USER");
        if (envUser != null && !envUser.isBlank()) dbUser = envUser;
        String envPass = System.getenv("HEALTHAI_DB_PASS");
        if (envPass != null) dbPassword = envPass;
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            try {
                Class.forName(dbDriver);
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver could not be loaded", e);
            }
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
