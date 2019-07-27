package com.project.twitter.controller;


import com.project.twitter.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping(value = "/login")
    public String logPage() {
        return "index";
    }

    @PostMapping(value = "/login")
    public String login(Model model, HttpServletRequest request, @RequestParam String email,
                        @RequestParam String pass) {
        if (loginService.login(email, pass, request)) {
            return "redirect:/getTweets";
        } else {
            model.addAttribute("messages", "Invalid login or password");
            return "index";
        }
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        loginService.logout(request);
        return "redirect:index.html";
    }
}