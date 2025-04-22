package com.ethicalhacking.filecmr.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ethicalhacking.filecmr.model.User;
import com.ethicalhacking.filecmr.service.UserService;


@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
	protected PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("password") String password,@RequestParam(value = "profileImage", required = false) MultipartFile profileImage, Model model) {
        
        List<String> randomEmailDomains = List.of("gmail.com", "yahoo.com", "hotmail.com");
        String randomEmailDomain = randomEmailDomains.get((int) (Math.random() * randomEmailDomains.size()));
        String randomEmail = username + "@" + randomEmailDomain;

        User user = new User();
        user.setUsername(username);
        user.setEmail(randomEmail);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userService.saveUserArtifact(user);

        // Salva l'immagine di profilo se presente
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // Percorso della cartella
                String dirPath = "/images/profiles/";
                String fileName = "user_" + savedUser.getId() + ".png";
                Path uploadPath = Paths.get(dirPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                profileImage.transferTo(filePath.toFile());
                user.setProfileImagePath(dirPath + "/" + fileName);

                userService.saveUserArtifact(savedUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            user.setProfileImagePath("/images/profiles/default.png");
            userService.saveUserArtifact(savedUser);
        }
        
        return "redirect:/login";
    }

    
}
