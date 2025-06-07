package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.ProjectDTO;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Role;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.ProjectService;
import com.freelancer.backend.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private final ModelMapper modelMapper = new ModelMapper(); // Para converter entidade ↔ DTO

    // GET /api/projects  → lista todos (ou filtra por status ?status=OPEN)
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects(
            @RequestParam(value = "status", required = false) String status) {
        List<Project> projects = projectService.getAllProjects(status);
        List<ProjectDTO> result = projects.stream()
                .map(p -> modelMapper.map(p, ProjectDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // GET /api/projects/{id} → detalhes do projeto
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        return ResponseEntity.ok(modelMapper.map(project, ProjectDTO.class));
    }

    // POST /api/projects → criar novo projeto (somente CONTRACTOR)
    @PostMapping
    public ResponseEntity<?> createProject(Authentication authentication,
                                           @Valid @RequestBody ProjectDTO projectDTO) {
        String username = authentication.getName();
        User contractor = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        // Converter DTO → entidade
        Project project = Project.builder()
                .title(projectDTO.getTitle())
                .description(projectDTO.getDescription())
                .budget(projectDTO.getBudget())
                .deadline(projectDTO.getDeadline())
                .contractor(contractor)
                .build();
        Project saved = projectService.createProject(project);
        return ResponseEntity.ok(modelMapper.map(saved, ProjectDTO.class));
    }

    // PUT /api/projects/{id} → atualizar (apenas quem criou)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(Authentication authentication,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ProjectDTO projectDTO) {
        Project existing = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        String username = authentication.getName();
        if (!existing.getContractor().getUsername().equals(username)) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Acesso negado."));
        }
        // Atualiza campos
        existing.setTitle(projectDTO.getTitle());
        existing.setDescription(projectDTO.getDescription());
        existing.setBudget(projectDTO.getBudget());
        existing.setDeadline(projectDTO.getDeadline());
        Project updated = projectService.updateProject(id, existing);
        return ResponseEntity.ok(modelMapper.map(updated, ProjectDTO.class));
    }

    // DELETE /api/projects/{id} → excluir (apenas quem criou ou admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(Authentication authentication,
                                           @PathVariable Long id) {
        Project existing = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        boolean isAdmin = user.getRoles().contains(Role.ROLE_ADMIN);
        if (!existing.getContractor().getUsername().equals(username) && !isAdmin) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Acesso negado."));
        }
        projectService.deleteProject(id);
        return ResponseEntity.ok(new ApiResponse(true, "Projeto deletado com sucesso."));
    }

    // GET /api/projects/contractor  → lista projetos do contratante atual
    @GetMapping("/contractor")
    public ResponseEntity<List<ProjectDTO>> getMyProjects(Authentication authentication) {
        String username = authentication.getName();
        User contractor = userService.findByUsername(username).orElseThrow();
        List<Project> projects = projectService.getProjectsByContractor(contractor);
        List<ProjectDTO> result = projects.stream()
                .map(p -> modelMapper.map(p, ProjectDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
