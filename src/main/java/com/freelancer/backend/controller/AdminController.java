package com.freelancer.backend.controller;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.ProjectService;
import com.freelancer.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas admins acessam
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    // GET /api/admin/users → lista todos usuários
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // GET /api/admin/projects → lista todos projetos
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects(null));
    }

    // GET /api/admin/applications → lista todas candidaturas (poderia criar método adicional em service)
    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications() {
        // Para simplificar, podemos invocar diretamente o repository
        // Mas como não temos um método no service, deixamos em aberto ou implementamos no futuro.
        return ResponseEntity.ok("Em implementação");
    }

    // ... outros endpoints administrativos ...
}
