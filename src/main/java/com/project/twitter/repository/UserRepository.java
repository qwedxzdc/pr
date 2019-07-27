package com.project.twitter.repository;

import com.project.twitter.entity.Tweet;
import com.project.twitter.entity.User;
import com.project.twitter.exeption.NotValidEmailException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.project.twitter.common.GeneralDateTimeUtil.formatTime;
import static com.project.twitter.common.GeneralUtil.getQueryBuilder;
import static com.project.twitter.repository.TweetRepository.getNumberLikes;

public class UserRepository {
    private static final Logger log = Logger.getLogger(UserRepository.class);
    private String QUERY_USER_TWEETS = "SELECT tweet.tweet_id, tweet.user_id, tweet.text, " +
            "tweet.created_at, tweet.image, user_info.user_name, user_info.user_id " +
            "FROM tweet JOIN user_info ON " +
            "user_info.user_id=tweet.user_id WHERE user_info.user_id = ANY(?) ORDER BY tweet.tweet_id DESC %s";
    private String QUERY_INITIAL_USER_TWEETS_FOR_INITIAL = "SELECT COUNT(tweet_id) FROM tweet WHERE user_id = ANY(?)";
    private static final String QUERY_SELECT_FOLLOWED = "SELECT followed_user_id FROM subscribed WHERE user_id = ? ";
    private static final String QUERY_NUMBER_ROWS = "SELECT COUNT(tweet_id) FROM tweet WHERE user_id= ?";
    private static final String QUERY_GET_USER_ID = "SELECT user_id FROM user_info WHERE email = ?";
    private static final String QUERY_GET_FOLLOWED_USERS = "SELECT ui.user_name," +
            "       ui.user_id" +
            " FROM user_info ui join subscribed s on ui.user_id = s.followed_user_id " +
            "WHERE s.user_id = ? " +
            "ORDER BY ui.user_id DESC " +
            "LIMIT 15;";

    private static final String QUERY_GET_NOT_FOLLOWED_USERS = "SELECT user_name, " +
            "       user_id " +
            " FROM user_info " +
            "WHERE user_id NOT IN (SELECT followed_user_id " +
            "                        FROM subscribed " +
            "                       WHERE user_id = ?) " +
            "  AND user_id != ?" +
            "ORDER BY user_id DESC " +
            "LIMIT 15";

    public static final String QUERY_IS_LIKED = "SELECT * FROM likes WHERE user_id = ? AND tweet_id= ?";

    public UserRepository() {
    }

    public List<User> getNotFollowedUsers(Long loggedUserId) throws SQLException,
            ClassNotFoundException {
        List<User> users = new ArrayList<>();
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_GET_NOT_FOLLOWED_USERS);
        preparedStatement.setLong(1, loggedUserId);
        preparedStatement.setLong(2, loggedUserId);
        ResultSet result = preparedStatement.executeQuery();
        if (result == null) {
            return users;
        }
        while (result.next()) {
            User user = new User();
            user.setId(result.getLong("user_id"));
            user.setName(result.getString("user_name"));
            users.add(user);
        }
        return users;
    }

    public List<User> getFollowedUsers(Long loggedUserId) throws SQLException,
            ClassNotFoundException {
        List<User> users = new ArrayList<>();
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_GET_FOLLOWED_USERS);
        preparedStatement.setLong(1, loggedUserId);
        ResultSet result = preparedStatement.executeQuery();
        if (result == null) {
            return users;
        }
        while (result.next()) {
            User user = new User();
            user.setId(result.getLong("user_id"));
            user.setName(result.getString("user_name"));
            users.add(user);
        }
        return users;
    }

    public ArrayList<Tweet> getTweetsForUser(int currentPage, int recordsPerPage, Long userId, boolean isUserPage,
                                             Long initialUserId)
            throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        int start = currentPage * recordsPerPage - recordsPerPage;
        ResultSet result;
        ArrayList<Tweet> tweetsFromOneUser = new ArrayList<>();
        Array sqlArray;
        if (isUserPage) {
            List<Long> followedIds = getFollowedIds(userId);
            sqlArray = conn.createArrayOf("INTEGER", followedIds.toArray());
        } else {
            sqlArray = conn.createArrayOf("INTEGER", new Long[]{userId});
        }
        String QUERY_SELECT_PAGINATION_TWEETS_FROM_USER = getQueryBuilder(QUERY_USER_TWEETS,
                recordsPerPage, start);
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_SELECT_PAGINATION_TWEETS_FROM_USER);
        preparedStatement.setArray(1, sqlArray);
        result = preparedStatement.executeQuery();

        if (result == null) {
            return tweetsFromOneUser;
        }
        while (result.next()) {
            Tweet tweet = new Tweet();
            tweet.setText(result.getString("text"));
            tweet.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
            tweet.setFormattedTime(formatTime(result.getTimestamp("created_at").toLocalDateTime()));
            tweet.setUserId(result.getLong("user_id"));
            tweet.setUserName(result.getString("user_name"));
            Long tweetId = result.getLong("tweet_id");
            tweet.setTweetId(tweetId);
            tweet.setImage(result.getString("image"));
            tweet.setLiked(isLiked(initialUserId, tweetId));
            tweet.setNumberOfLikes(getNumberLikes(tweetId));
            tweetsFromOneUser.add(tweet);
        }
        return tweetsFromOneUser;
    }

    private boolean isLiked(Long userId, Long tweetId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_IS_LIKED);
        preparedStatement.setLong(1, userId);
        preparedStatement.setLong(2, tweetId);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public List<Long> getFollowedIds(Long userId) throws SQLException, ClassNotFoundException {
        List<Long> userFollowedIds = new ArrayList<>();
        userFollowedIds.add(userId);
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_SELECT_FOLLOWED);
        preparedStatement.setLong(1, userId);
        ResultSet result = preparedStatement.executeQuery();
        while (result.next()) {
            userFollowedIds.add(result.getLong("followed_user_id"));
        }
        return userFollowedIds;
    }

    public int getNumberOfRows(Long initialUserId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_NUMBER_ROWS);
        preparedStatement.setLong(1, initialUserId);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        return result.getInt(1);
    }

    public int getNumberOfRowsForInitial(Long initialUserId) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        List<Long> followedIds = getFollowedIds(initialUserId);
        Array sqlArray = conn.createArrayOf("INTEGER", followedIds.toArray());
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_INITIAL_USER_TWEETS_FOR_INITIAL);
        preparedStatement.setArray(1, sqlArray);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        return result.getInt("count");
    }

    public static Long getUserId(String email) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionToDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(QUERY_GET_USER_ID);
        preparedStatement.setString(1, email);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        return (long) result.getInt(1);
    }

    public static Long getUserIdByEmail(String email) {
        Long userId = null;
        try {
            if (email == null) {
                throw new NotValidEmailException("Email in session contains null value");
            }
            userId = getUserId(email);
        } catch (Exception ex) {
            log.error("Exception", ex);
        }
        return userId;
    }
}