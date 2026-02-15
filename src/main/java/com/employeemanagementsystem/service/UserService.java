package com.employeemanagementsystem.service;

import com.employeemanagementsystem.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    long getTotalUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User updateUser(User user);
    void deleteUser(Long id);
}