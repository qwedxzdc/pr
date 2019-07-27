package com.project.twitter.repository;


import com.project.twitter.entity.Tweet;
import com.project.twitter.validator.ValidateResult;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.twitter.validator.TweetValidator.getValidateTextResult;


public class TweetRepository {
    private final static Logger log = Logger.getLogger(TweetRepository.class);
    private final static String QUERY_ADD_TWEET =
            "INSERT INTO tweet(user_id, text, created_at, image) " +
                    "VALUES ( ?, ?, ?, ?)";
    private static final String QUERY_DELETE_TWEET =
            "DELETE FROM tweet WHERE tweet_id = ?";
    private static final String QUERY_LIKE_TWEET =
            "UPDATE tweet SET number_likes = ? WHERE tweet_id = ?";
    private static final String QUERY_GET_LIKES =
            "SELECT number_likes FROM tweet WHERE tweet_id = ?";
    private final static String QUERY_INSERT_LIKE_RELATION =
            "INSERT INTO likes(user_id, tweet_id) " +
                    "VALUES ( ?, ?)";
    private static final String QUERY_DELETE_LIKE_RELATION =
            "DELETE FROM likes WHERE tweet_id = ? AND user_id = ?";

    public Map<String, String> saveTweet(Tweet tweet, Long userId) {
        Map<String, String> messages = new HashMap<>();
        try {
            ValidateResult textValid = getValidateTextResult(tweet.getText());
            if (textValid.isValid()) {
                Connection conn = ConnectionToDB.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(QUERY_ADD_TWEET);
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, StringEscapeUtils.escapeHtml4(tweet.getText()));
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(4, tweet.getImage());
                preparedStatement.executeUpdate();
                messages.putAll(textValid.getMessages());
            }
            messages.putAll(textValid.getMessages());
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Error saving tweet", ex);
        }
        return messages;
    }

    public void deleteTweet(Long tweetId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_DELETE_TWEET);
        preparedStatement.setLong(1, tweetId);
        preparedStatement.executeUpdate();
    }

    public void likeTweet(boolean likeValue, Long tweetId, Long userId) throws SQLException, ClassNotFoundException {
        Long numberOfLikes = getNumberLikes(tweetId);
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_LIKE_TWEET);
        preparedStatement.setLong(2, tweetId);
        if (likeValue) {
            preparedStatement.setLong(1,  ++numberOfLikes);
            insertLikeRelation(userId, tweetId);
        } else {
            preparedStatement.setLong(1, --numberOfLikes);
            deleteLikeRelation(userId, tweetId);
        }
        preparedStatement.executeUpdate();
    }

    private void insertLikeRelation(Long initialUserId, Long tweetId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_INSERT_LIKE_RELATION);
        preparedStatement.setLong(1, initialUserId);
        preparedStatement.setLong(2, tweetId);
        preparedStatement.executeUpdate();
    }

    private void deleteLikeRelation(Long initialUserId, Long tweetId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_DELETE_LIKE_RELATION);
        preparedStatement.setLong(1, tweetId);
        preparedStatement.setLong(2, initialUserId);
        preparedStatement.executeUpdate();
    }

    public static Long getNumberLikes(Long tweetId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_GET_LIKES);
        preparedStatement.setLong(1, tweetId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("number_likes");
        } else {
            return 0L;
        }
    }
}