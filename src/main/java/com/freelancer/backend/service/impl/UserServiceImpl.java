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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        // Verificar se username ou email já existe
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Erro: Username já está em uso!");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        // Criar usuário com dados básicos
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .fullName(signupRequest.getFullName())
                .bio(signupRequest.getBio())
                .skills(signupRequest.getSkills())
                .build();

        // Definir roles (padrão = ROLE_FREELANCER; mas pode vir na requisição)
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(Role.ROLE_FREELANCER);
        } else {
            strRoles.forEach(r -> {
                switch (r) {
                    case "admin":
                        roles.add(Role.ROLE_ADMIN);
                        break;
                    case "contractor":
                        roles.add(Role.ROLE_CONTRACTOR);
                        break;
                    case "freelancer":
                    default:
                        roles.add(Role.ROLE_FREELANCER);
                }
            });
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setFullName(userData.getFullName());
        user.setBio(userData.getBio());
        user.setSkills(userData.getSkills());
        // Se quiser permitir troca de senha:
        if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        // Não permitimos mudar roles aqui (poderia ter endpoint à parte para admin)
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
