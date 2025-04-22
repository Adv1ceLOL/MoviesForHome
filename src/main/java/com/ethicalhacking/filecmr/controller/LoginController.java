package com.ethicalhacking.filecmr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ethicalhacking.filecmr.model.User;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
}
