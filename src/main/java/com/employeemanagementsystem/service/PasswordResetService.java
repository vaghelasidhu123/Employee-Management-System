package com.employeemanagementsystem.service;

import com.employeemanagementsystem.model.PasswordResetToken;
import com.employeemanagementsystem.model.User;
import com.employeemanagementsystem.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createPasswordResetToken(User user) {
        // Invalidate existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Create new token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        tokenRepository.save(token);

        // In a real application, you would send an email here
        // For this example, we'll just print the token
        System.out.println("Password Reset Token for " + user.getEmail() + ": " + token.getToken());
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            return false;
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    public void deleteExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}