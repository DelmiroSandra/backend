package com.freelancer.backend.service;

import com.freelancer.backend.dto.ApplicationDTO;
import com.freelancer.backend.model.Application;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    Application applyToProject(User freelancer, Project project, String coverLetter);

    Optional<Application> getApplicationById(Long id);

    List<Application> getApplicationsByProject(Project project);
    List<Application> getApplicationsByFreelancer(User freelancer);
    ApplicationDTO processApplication(Long id, String newStatus, String username, boolean isAdmin);

    @Transactional
    Application processApplication(Long id, String newStatus);
}
