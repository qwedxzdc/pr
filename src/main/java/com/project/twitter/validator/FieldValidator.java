package com.project.twitter.validator;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.twitter.common.EncryptPassword.encryptPassword;
import static com.project.twitter.repository.ProfileRepository.getPassword;
import static com.project.twitter.repository.ProfileRepository.isEmailExist;


public class FieldValidator {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", Pattern.CASE_INSENSITIVE);
    public static final int MIN_FIELD_LENGTH = 3;
    public static final int MAX_FIELD_LENGTH = 20;

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validateFieldsWithEmailExist(String userName, String email, HttpServletRequest request)
            throws SQLException, ClassNotFoundException {
        if (isEmailExist(email)) {
            request.setAttribute("messages", "Already registered user with this email");
            return false;
        }
        return validateFieldsWithoutEmailExist(userName, email, request);
    }

    public static boolean validateFieldsWithoutEmailExist(String userName, String email,
                                                              HttpServletRequest request) {
        if (userName.length() < MIN_FIELD_LENGTH || userName.length() > MAX_FIELD_LENGTH) {
            request.setAttribute("messages", "Update failed! Name must be longer than " +
                    MIN_FIELD_LENGTH + " characters and shorter than " + MAX_FIELD_LENGTH + " characters");
            return false;
        }
        if (!validateEmail(email)) {
            request.setAttribute("messages", "Update failed! Required a valid email");
            return false;
        }
        return true;
    }

    public static boolean validatePasswordFields(String password, HttpServletRequest request) {
        if (password.length() < MIN_FIELD_LENGTH || password.length() > MAX_FIELD_LENGTH) {
            request.setAttribute("messages", "Update failed! Password must be longer than " +
                    MIN_FIELD_LENGTH + " characters and shorter than " + MAX_FIELD_LENGTH + " characters");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(String password, String email) throws SQLException,
            ClassNotFoundException, NoSuchAlgorithmException {
        password = encryptPassword(password);
        return password.equals(getPassword(email));
    }
}