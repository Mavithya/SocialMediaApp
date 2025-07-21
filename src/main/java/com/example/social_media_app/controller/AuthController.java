package com.example.social_media_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Show the custom login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}