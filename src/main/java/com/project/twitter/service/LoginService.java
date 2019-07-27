package com.project.twitter.service;

import com.project.twitter.repository.LoginRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import static com.project.twitter.common.EncryptPassword.encryptPassword;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Service("loginService")
public class LoginService {
    private static final Logger log = Logger.getLogger(LoginService.class);
    private LoginRepository loginRepository = new LoginRepository();

    public boolean login(String email, String pass, HttpServletRequest request) {
        try {
            email = escapeHtml4(email);
            String password = escapeHtml4(pass);
            password = encryptPassword(password);
            ResultSet resultSet = loginRepository.login(email, password);
            if (resultSet.next()) {
                request.getSession().setAttribute("email", email);
                return true;
            }
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException ex) {
            log.error("Exception", ex);
        }
        return false;
    }

    public void logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("email", null);
            session.invalidate();
        } catch (Exception ex) {
            log.error("Exception", ex);
        }
    }
}