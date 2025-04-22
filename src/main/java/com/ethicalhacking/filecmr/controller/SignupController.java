package com.ethicalhacking.filecmr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignupController {
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup"; // This will render signup.html from templates
    }
}