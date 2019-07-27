package com.project.twitter.controller;

import com.project.twitter.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubscribeController {
    private final SubscribeService subscribeService;

    @Autowired
    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @PostMapping(value = "/subscribe")
    public String userSubscribe(@RequestParam Long userId, @RequestParam Long initialUserId) {
        subscribeService.subscribe(userId, initialUserId);
        return "redirect:/getTweets";
    }
}