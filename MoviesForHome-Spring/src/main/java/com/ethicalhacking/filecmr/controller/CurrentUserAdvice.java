package com.ethicalhacking.filecmr.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ethicalhacking.filecmr.model.User;

@ControllerAdvice
public class CurrentUserAdvice {
    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return user;
    }
}
