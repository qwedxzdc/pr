package com.project.twitter.service;

import com.project.twitter.repository.ProfileRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import static com.project.twitter.common.EncryptPassword.encryptPassword;
import static com.project.twitter.validator.FieldValidator.*;
import static com.project.twitter.validator.FieldValidator.validatePasswordFields;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Service("profileService")
public class ProfileService {
    private static final Logger log = Logger.getLogger(ProfileService.class);
    private ProfileRepository profileRepository = new ProfileRepository();

    public ModelMap getProfile(String email, HttpServletRequest request) {
        ModelMap model = new ModelMap();
        try {
            email = escapeHtml4(email);
            HttpSession session = request.getSession();
            ResultSet result = profileRepository.getProfile(email);
            if (result.next()) {
                Long oldId = result.getLong("user_id");
                String oldName = result.getString("user_name");
                session.setAttribute("email", email);
                session.setAttribute("user_id", oldId);
                model.addAttribute("oldName", oldName);
                model.addAttribute("oldEmail", email);
            } else {
                model.addAttribute("errorMessage", "Error during processing profile information");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Exception", ex);
        }
        return model;
    }

    public ModelMap updateProfile(String email, HttpServletRequest request,
                                  String oldEmail, String user_name) {
        ModelMap model = new ModelMap();
        try {
            HttpSession session = request.getSession();
            String userName = escapeHtml4(user_name);
            email = escapeHtml4(email);
            oldEmail = escapeHtml4(oldEmail);
            if (email.equals(oldEmail)) {
                if (validateFieldsWithoutEmailExist(userName, email, request)) {
                    model.mergeAttributes(updateNameEmail(email, userName, oldEmail, session));
                } else {
                    model.addAttribute("errorMessage", "Update name-email failed!");
                }
            } else {
                if (validateFieldsWithEmailExist(userName, email, request)) {
                    model.mergeAttributes(updateNameEmail(email, userName, oldEmail, session));
                } else {
                    model.addAttribute("errorMessage", "Update name-email failed!");
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Exception", ex);
        }
        email = (String) request.getSession().getAttribute("email");
        model.mergeAttributes(getProfile(email, request));
        return model;
    }

    private ModelMap updateNameEmail(String newEmail, String user_name, String email,
                                     HttpSession session) throws SQLException, ClassNotFoundException {
        ModelMap model = new ModelMap();
        int result = profileRepository.updateProfile(newEmail, user_name, email);
        if (result > 0) {
            session.setAttribute("email", newEmail);
            return model.addAttribute("completeMessage", "Information has been updated");
        } else {
            return model.addAttribute("errorMessage", "Update name-email failed!");
        }
    }

    public ModelMap validateAndUpdatePassword(String email, String oldPassword, String password,
                                              String confirmPassword, HttpServletRequest request) {
        ModelMap model = new ModelMap();
        try {
            email = escapeHtml4(email);
            oldPassword = escapeHtml4(oldPassword);
            password = escapeHtml4(password);
            confirmPassword = escapeHtml4(confirmPassword);
            if (validatePassword(oldPassword, email)) {
                if (!password.equals(confirmPassword)) {
                    model.addAttribute("messages", "Confirmed password does not match");
                } else {
                    if (validatePasswordFields(password, request)) {
                        model.mergeAttributes(updatePassword(password, email));
                        model.addAttribute("messages", "Password successfully updated");
                    }
                }
            } else {
                model.addAttribute("messages", "Old password does not match");
            }
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException ex) {
            log.error("Exception", ex);
        }
        return model;
    }

    private ModelMap updatePassword(String password, String email) throws SQLException, ClassNotFoundException,
            NoSuchAlgorithmException {
        password = encryptPassword(password);
        int result = profileRepository.updatePassword(password, email);
        ModelMap model = new ModelMap();
        if (result > 0) {
            model.addAttribute("messages", "Information has been updated");
        } else {
            model.addAttribute("messages", "Update password failed!");
        }
        return model;
    }
}