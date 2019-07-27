package com.project.twitter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationRepository {
    private static final String QUERY_ADD_USER =
            "INSERT INTO user_info (email, user_name, password) VALUES ( ?,  ?, ?)";

    public void createProfile(String email, String user, String password) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_ADD_USER);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, user);
        preparedStatement.setString(3, password);
        preparedStatement.executeUpdate();
    }
}