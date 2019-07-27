package com.project.twitter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscribeRepository {
    private static final String QUERY_IS_ALREADY_WUBSCRIBED = "SELECT * FROM subscribed " +
            "WHERE user_id = ? AND  followed_user_id= ?";
    private static final String QUERY_SUBSCRIBE = "INSERT INTO subscribed (user_id, followed_user_id) VALUES ( ?,  ?)";
    private static final String QUERY_UNSUBSCRIBE = "DELETE FROM subscribed WHERE user_id = ? AND  followed_user_id= ?";

    public static boolean isUserAlreadySubscribed(Long subscribedUserId, Long userId) throws SQLException,
            ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_IS_ALREADY_WUBSCRIBED);
        preparedStatement.setLong(1, userId);
        preparedStatement.setLong(2, subscribedUserId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public void subscribe(Long subscribedUserId, Long userId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_SUBSCRIBE);
        preparedStatement.setLong(1, userId);
        preparedStatement.setLong(2, subscribedUserId);
        preparedStatement.executeUpdate();
    }

    public void unsubscribe(Long subscribedUserId, Long userId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_UNSUBSCRIBE);
        preparedStatement.setLong(1, userId);
        preparedStatement.setLong(2, subscribedUserId);
        preparedStatement.executeUpdate();
    }
}