package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.ApplicationDTO;
import com.freelancer.backend.model.Application;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Role;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.ApplicationService;
import com.freelancer.backend.service.ProjectService;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired private ApplicationService applicationService;
    @Autowired private UserService userService;
    @Autowired private ProjectService projectService;

    @PostMapping
    public ResponseEntity<?> apply(Authentication auth,
                                   @Valid @RequestBody ApplicationDTO dto) {
        User u = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!u.getRoles().contains(Role.ROLE_FREELANCER)) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Apenas freelancers podem se candidatar."));
        }
        Project p = projectService.getProjectById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        Application out = applicationService.applyToProject(u, p, dto.getCoverLetter());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> listByProject(Authentication auth,
                                           @PathVariable Long projectId) {
        Project p = projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        User u = userService.findByUsername(auth.getName()).orElseThrow();
        boolean admin = u.getRoles().contains(Role.ROLE_ADMIN);
        boolean owner = p.getContractor().getUsername().equals(u.getUsername());
        if (!admin && !owner) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse(false, "Acesso negado."));
        }
        List<Application> list = applicationService.getApplicationsByProject(p);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/freelancer")
    public ResponseEntity<List<Application>> listMine(Authentication auth) {
        User u = userService.findByUsername(auth.getName()).orElseThrow();
        List<Application> list = applicationService.getApplicationsByFreelancer(u);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> process(Authentication auth,
                                     @PathVariable Long id,
                                     @RequestParam String status) {
        User u = userService.findByUsername(auth.getName()).orElseThrow();
        boolean admin = u.getRoles().contains(Role.ROLE_ADMIN);
        ApplicationDTO updated = applicationService.processApplication(id, status, u.getUsername(), admin);
        return ResponseEntity.ok(updated);
    }
}
