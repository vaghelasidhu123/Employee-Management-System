package com.employeemanagementsystem.controller;

import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Register");
        return "register";
    }

    @PostMapping("/process-register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "redirect:/login?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "My Profile");
        }
        return "profile";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access_denied";
    }
}