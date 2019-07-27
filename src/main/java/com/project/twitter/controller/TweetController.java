package com.project.twitter.controller;

import com.project.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TweetController {
    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping(value = "/like")
    public String likeTweet( @RequestParam Long userId,
                            @RequestParam Long tweetId, @RequestParam Boolean like,
                            @RequestParam Long initialUserId, @RequestParam Long currentPage,
                            RedirectAttributes redirectAttributes) {
        ModelMap model = tweetService.likeTweet(like, tweetId, userId, initialUserId, currentPage);
        redirectAttributes.addAllAttributes(model);
        return "redirect:/getTweets";
    }

    @GetMapping(value = "/deleteTweet")
    public String deleteTweet(@SessionAttribute("email") String email, @RequestParam Long userId,
                              @RequestParam Long tweetId) {
        tweetService.deleteTweet(email, userId, tweetId);
        return "redirect:/getTweets";
    }
}