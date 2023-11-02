package com.denismiagkov.walletservice.init;

import com.denismiagkov.walletservice.init.PropertyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    @Autowired
    public DatabaseConnection() {
        this.URL = PropertyFile.getProperties("URL");
        this.USERNAME = PropertyFile.getProperties("USER_NAME");
        this.PASSWORD = PropertyFile.getProperties("USER_PASSWORD");
    }

    public DatabaseConnection(String url, String username, String password) {
        this.URL = url;
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return DriverManager.getConnection(this.URL, this.USERNAME, this.PASSWORD);
    }
}

