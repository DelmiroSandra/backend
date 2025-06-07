package com.freelancer.backend.service;

import com.freelancer.backend.dto.SignupRequest;
import com.freelancer.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(SignupRequest signupRequest);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User updateUser(Long id, User userData);
    void deleteUser(Long id);
}
