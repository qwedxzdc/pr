package com.project.twitter.controller;

import com.project.twitter.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping(value = "/registration")
    public String viewRegistration() {
        return "registration";
    }

    @PostMapping(value = "/registration")
    public ModelAndView registration(
            HttpServletRequest request, @RequestParam("email") String email,
            @RequestParam String user_name, @RequestParam String password, @RequestParam String confirm_password
    ) {
        ModelMap model = registrationService.registration(request, user_name, password, confirm_password, email);
        if (model == null) {
            return new ModelAndView("getTweets");
        } else {
            return new ModelAndView("registration", model);
        }
    }
}