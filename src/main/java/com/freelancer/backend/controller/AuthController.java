package com.freelancer.backend.controller;

import com.freelancer.backend.config.JwtTokenUtil;
import com.freelancer.backend.dto.*;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;   // 1) injete o encoder

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            userService.registerUser(signUpRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Usuário registrado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 2) Carrega dados do usuário do banco
            User userFromDb = userService.findByUsername(loginRequest.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // 3) DEBUG prints
            System.out.println("DEBUG rawPassword:     " + loginRequest.getPassword());
            System.out.println("DEBUG encodedPassword: " + userFromDb.getPassword());
            System.out.println("DEBUG matches:         " +
                    passwordEncoder.matches(loginRequest.getPassword(), userFromDb.getPassword()));

            // 4) Autentica com Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 5) Gera JWT
            String jwt = jwtTokenUtil.generateJwtToken(authentication);

            // 6) Busca dados completos do usuário para a resposta
            User user = userService.findByUsername(
                    ((org.springframework.security.core.userdetails.User)
                            authentication.getPrincipal()).getUsername()
            ).orElseThrow();

            LoginResponse response = LoginResponse.builder()
                    .token(jwt)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Credenciais inválidas."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Erro interno: " + e.getMessage()));
        }
    }
}
