package com.project.twitter.controller;


import com.project.twitter.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
public class UpdatePasswordController {
    private final ProfileService profileService;

    @Autowired
    public UpdatePasswordController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(value = "/updatePassword")
    public String viewPasswordPage(@SessionAttribute("email") String email, HttpServletRequest request) {
        request.setAttribute("email", email);
        return "updatePassword";
    }

    @PostMapping(value = "/updatePassword")
    public ModelAndView doPost(HttpServletRequest request, @RequestParam String email,
                               @RequestParam String oldPassword, @RequestParam String password,
                               @RequestParam String confirmPassword) {
        request.setAttribute("email", email);
        ModelMap model = profileService.validateAndUpdatePassword(email, oldPassword, password, confirmPassword, request);
        return new ModelAndView("updatePassword", model);
    }
}