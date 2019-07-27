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
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(value = "/profile")
    public ModelAndView viewProfile(HttpServletRequest request, @SessionAttribute("email") String email) {
        ModelMap model = profileService.getProfile(email, request);
        return new ModelAndView("profile", model);
    }

    @PostMapping(value = "/profile")
    public ModelAndView updateProfile(HttpServletRequest request, @SessionAttribute("email") String oldEmail,
                                         @RequestParam String user_name, @RequestParam("user_email") String email) {
        ModelMap model = profileService.updateProfile(email, request, oldEmail, user_name);
        return new ModelAndView("profile", model);
    }
}