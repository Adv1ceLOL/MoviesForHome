package com.ethicalhacking.filecmr.authentication;

import com.ethicalhacking.filecmr.repository.UserRepository;
import com.ethicalhacking.filecmr.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.annotation.PostConstruct;

@Configuration
public class PasswordResetOnStartup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void resetPasswords() {
        for (User user : userRepository.findAll()) {
            if (user.getIsAdmin()) {
                user.setPassword(passwordEncoder.encode("pass123admin"));
            } else {
                user.setPassword(passwordEncoder.encode("password123"));
            }
            //userRepository.save(user);
        }
        System.out.println("All user passwords have been reset.");
    }
}