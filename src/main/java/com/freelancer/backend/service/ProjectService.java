package com.freelancer.backend.service;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);
    Optional<Project> getProjectById(Long id);
    List<Project> getAllProjects(String status);
    List<Project> getProjectsByContractor(User contractor);
    Project updateProject(Long id, Project projectData);
    void deleteProject(Long id);
}
