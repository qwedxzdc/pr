package com.project.twitter.service;

import com.project.twitter.repository.RegistrationRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import static com.project.twitter.common.EncryptPassword.encryptPassword;
import static com.project.twitter.validator.FieldValidator.validateFieldsWithEmailExist;
import static com.project.twitter.validator.FieldValidator.validatePasswordFields;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Service("registrationService")
public class RegistrationService {
    private static final Logger log = Logger.getLogger(RegistrationService.class);
    private RegistrationRepository registrationRepository = new RegistrationRepository();

    public ModelMap registration(HttpServletRequest request, String user_name,
                                String password, String confirm_password,
                                String email) {
        ModelMap model = new ModelMap() ;
        try {
            HttpSession session = request.getSession();
            String user = escapeHtml4(user_name);
            password = escapeHtml4(password);
            String confirmPassword = escapeHtml4(confirm_password);
            email = escapeHtml4(email);
            if (!password.equals(confirmPassword)) {
                model.addAttribute("messages", "Passwords does not match");
                return model;
            }
            if (validateFieldsWithEmailExist(user, email, request) && validatePasswordFields(password,
                    request)) {
                password = encryptPassword(password);
                registrationRepository.createProfile(email, user, password);
                session.setAttribute("email", email);
                return model;
            }

        } catch (SQLException | ClassNotFoundException |
                NoSuchAlgorithmException ex) {
            log.error("Exception", ex);
        }
        model.addAttribute("messages",
                "User with this email already exist or password doesnt valid");
        return model;
    }
}
