package com.employeemanagementsystem.controller;

import com.employeemanagementsystem.model.PasswordResetToken;
import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.service.PasswordResetService;
import com.employeemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "reset", required = false) String reset,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        if (reset != null) {
            model.addAttribute("message", "Password reset successful! Please login with your new password.");
        }
        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Register");
        return "register";
    }

    @PostMapping("/process-register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Register");
            return "register";
        }

        try {
            // Check if username exists
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists!");
                model.addAttribute("pageTitle", "Register");
                return "register";
            }

            // Check if email exists
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already registered!");
                model.addAttribute("pageTitle", "Register");
                return "register";
            }

            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "redirect:/login?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Register");
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "Forgot Password");
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOptional = userService.findByEmail(email);
            if (userOptional.isPresent()) {
                passwordResetService.createPasswordResetToken(userOptional.get());
                redirectAttributes.addFlashAttribute("success",
                        "Password reset instructions have been sent to your email.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No account found with that email address.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error sending reset email. Please try again.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password/{token}")
    public String showResetPasswordForm(@PathVariable("token") String token,
                                        Model model) {
        PasswordResetToken resetToken = passwordResetService.getPasswordResetToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("token", token);
        model.addAttribute("pageTitle", "Reset Password");
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/reset-password/" + token;
            }

            boolean success = passwordResetService.resetPassword(token, password);
            if (success) {
                redirectAttributes.addFlashAttribute("success",
                        "Password reset successfully! Please login.");
                return "redirect:/login?reset=true";
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Invalid or expired reset token.");
                return "redirect:/forgot-password";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error resetting password. Please try again.");
            return "redirect:/reset-password/" + token;
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

                // Verify current password
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
                    return "redirect:/change-password";
                }

                // Update password
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

            // Update allowed fields
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