package com.freelancer.backend.controller;

import com.freelancer.backend.config.JwtTokenUtil;
import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.LoginRequest;
import com.freelancer.backend.dto.LoginResponse;
import com.freelancer.backend.dto.SignupRequest;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    // POST /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User user = userService.registerUser(signUpRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Usuário registrado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateJwtToken(authentication);

            User userDetails = (User) authentication.getPrincipal();
            // ATENÇÃO: no override de loadUserByUsername, retornamos um org.springframework.security.core.userdetails.User
            // Para recuperar o ID / email, fazemos outra busca:
            User user = userService.findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername())
                    .orElseThrow();

            LoginResponse response = LoginResponse.builder()
                    .token(jwt)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponse(false, "Credenciais inválidas."));
        }
    }
}
