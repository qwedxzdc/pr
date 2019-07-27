package com.project.twitter.controller;

import com.project.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class GetTweetsController {
    private TweetService tweetService;

    @Autowired
    GetTweetsController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping(value = "/getTweets")
    public ModelAndView viewTweetsPage(@SessionAttribute("email") String email,
                                       @RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) Integer currentPage) {
        ModelMap model = tweetService.getTweetsPerPage(email, userId, currentPage);
        return new ModelAndView("listTweets", model);
    }

    @PostMapping(value = "/getTweets")
    public ModelAndView postTweet(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @SessionAttribute("email") String email,
                                  @RequestParam(required = false) Integer currentPage) {
        ModelMap model = tweetService.postTweet(request, response, email, currentPage);
        return new ModelAndView("listTweets", model);
    }
}