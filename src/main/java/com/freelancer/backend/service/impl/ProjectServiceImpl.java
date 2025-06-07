package com.freelancer.backend.service.impl;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.ProjectRepository;
import com.freelancer.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired private ProjectRepository projectRepository;

    @Override @Transactional
    public Project createProject(Project project) {
        project.setStatus("OPEN");
        return projectRepository.save(project);
    }

    @Override public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Override public List<Project> getAllProjects(String status) {
        if (status == null || status.isBlank()) {
            return projectRepository.findAll();
        }
        return projectRepository.findByStatus(status);
    }

    @Override public List<Project> getProjectsByContractor(User contractor) {
        return projectRepository.findByContractor(contractor);
    }

    @Override @Transactional
    public Project updateProject(Long id, Project data) {
        Project p = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        p.setTitle(data.getTitle());
        p.setDescription(data.getDescription());
        p.setBudget(data.getBudget());
        p.setDeadline(data.getDeadline());
        return projectRepository.save(p);
    }

    @Override public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
