package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users/me  → retorna dados do usuário autenticado
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName(); // vem do JWT
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/me  → atualizar perfil do usuário autenticado
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(Authentication authentication,
                                               @Valid @RequestBody User userData) {
        String username = authentication.getName();
        User existing = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        User updated = userService.updateUser(existing.getId(), userData);
        return ResponseEntity.ok(updated);
    }

    // GET /api/users → (apenas admin) lista todos usuários
    @GetMapping
    // Somente ROLE_ADMIN (ver anotação no SecurityConfig)
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // DELETE /api/users/{id} → (apenas admin) remover usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "Usuário deletado com sucesso."));
    }
}
