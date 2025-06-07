package com.freelancer.backend.service.impl;

import com.freelancer.backend.dto.SignupRequest;
import com.freelancer.backend.model.Role;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.UserRepository;
import com.freelancer.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override @Transactional
    public User registerUser(SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username em uso");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email em uso");
        }
        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .bio(req.getBio())
                .skills(req.getSkills())
                .build();
        Set<Role> roles = new HashSet<>();
        if (req.getRoles() == null || req.getRoles().isEmpty()) {
            roles.add(Role.ROLE_FREELANCER);
        } else {
            req.getRoles().forEach(r -> {
                switch (r.toLowerCase()) {
                    case "admin": roles.add(Role.ROLE_ADMIN); break;
                    case "contractor": roles.add(Role.ROLE_CONTRACTOR); break;
                    default: roles.add(Role.ROLE_FREELANCER);
                }
            });
        }
        u.setRoles(roles);
        return userRepository.save(u);
    }

    @Override public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override public Optional<User> findByUsername(String uname) {
        return userRepository.findByUsername(uname);
    }

    @Override public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override @Transactional
    public User updateUser(Long id, User data) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setFullName(data.getFullName());
        u.setBio(data.getBio());
        u.setSkills(data.getSkills());
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return userRepository.save(u);
    }

    @Override public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
