package com.project.twitter.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionToDB {
    private static String url = "jdbc:postgresql://localhost:5432/Tweet";
    private static String user = "twitter_user";
    private final static String password = "Snob2323";
    private static Connection connection;

    private ConnectionToDB() {
    }

    private static Connection connection(String url, String user, String password) throws SQLException,
            ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            connection = connection(url, user, password);
        }
        return connection;
    }
}