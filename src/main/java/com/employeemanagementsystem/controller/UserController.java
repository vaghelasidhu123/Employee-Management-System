package com.employeemanagementsystem.controller;

import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/edit-profile")
    public String showEditProfileForm(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit Profile");
        }
        return "edit_profile";
    }

    @PostMapping("/edit-profile")
    public String processEditProfile(@Valid @ModelAttribute("user") User updatedUser,
                                     BindingResult result,
                                     Principal principal,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Profile");
            return "edit_profile";
        }
        try {
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setEmail(updatedUser.getEmail());
            userService.updateUser(currentUser);
            model.addAttribute("success", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Edit Profile");
            return "edit_profile";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Change Password");
        return "change_password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("currentPassword") String currentPassword,
                                        @RequestParam("newPassword") String newPassword,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
                return "redirect:/change-password";
            }
            Optional<User> userOptional = userService.findByUsername(principal.getName());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
                    return "redirect:/change-password";
                }
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
                return "redirect:/profile";
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/change-password";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
            return "redirect:/change-password";
        }
    }


}
