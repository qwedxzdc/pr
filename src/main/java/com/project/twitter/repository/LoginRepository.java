package com.project.twitter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {
    private static final String QUERY_LOGIN =
            "SELECT * FROM user_info WHERE email = ? AND password = ?";

    public ResultSet login(String email, String password) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_LOGIN);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, password);
        return preparedStatement.executeQuery();
    }
}