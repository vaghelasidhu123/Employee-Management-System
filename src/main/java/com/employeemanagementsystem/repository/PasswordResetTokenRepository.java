package com.employeemanagementsystem.repository;

import com.employeemanagementsystem.model.PasswordResetToken;
import com.employeemanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    List<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
    void deleteAllByExpiryDateBefore(LocalDateTime date);
}