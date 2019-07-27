package com.project.twitter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileRepository {
    private static final String QUERY_PROFILE =
            "SELECT * FROM user_info WHERE email = ?";
    private static final String QUERY_NAME_EMAIL_UPDATE =
            "UPDATE user_info SET email = ?, user_name = ? WHERE email = ?";
    private static final String QUERY_PASSWORD_UPDATE =
            "UPDATE user_info SET password = ? WHERE email = ?";
    private static final String QUERY_EXIST_EMAIL =
            "SELECT * FROM user_info WHERE email = ?";
    private static final String QUERY_GET_OLD_PASSWORD =
            "SELECT password FROM user_info WHERE email = ?";

    public ResultSet getProfile(String email) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_PROFILE);
        preparedStatement.setString(1, email);
        return preparedStatement.executeQuery();
    }

    public int updateProfile(String email, String user_name, String oldEmail)
            throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_NAME_EMAIL_UPDATE);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, user_name);
        preparedStatement.setString(3, oldEmail);
        return preparedStatement.executeUpdate();
    }

    public int updatePassword(String password, String email)
            throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_PASSWORD_UPDATE);
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, email);
        return preparedStatement.executeUpdate();
    }

    public static boolean isEmailExist(String user_email) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_EXIST_EMAIL);
        preparedStatement.setString(1, user_email);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public static String getPassword(String user_email) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_GET_OLD_PASSWORD);
        preparedStatement.setString(1, user_email);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("password");
        } else {
            return null;
        }
    }
}