package com.freelancer.backend.service.impl;


import com.freelancer.backend.dto.ApplicationDTO;
import com.freelancer.backend.model.Application;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.ApplicationRepository;
import com.freelancer.backend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationServiceImpl implements ApplicationService{
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public Application applyToProject(User freelancer, Project project, String coverLetter) {
        // Verificar se já existe candidatura
        applicationRepository.findByFreelancerAndProject(freelancer, project).ifPresent(a -> {
            throw new RuntimeException("Você já se candidatou a este projeto.");
        });

        Application application = Application.builder()
                .freelancer(freelancer)
                .project(project)
                .coverLetter(coverLetter)
                .status("PENDING")
                .build();
        return applicationRepository.save(application);
    }

    @Override
    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public List<Application> getApplicationsByProject(Project project) {
        return applicationRepository.findByProject(project);
    }

    @Override
    public List<Application> getApplicationsByFreelancer(User freelancer) {
        return applicationRepository.findByFreelancer(freelancer);
    }

    @Override
    public ApplicationDTO processApplication(Long id, String newStatus, String username, boolean isAdmin) {
        return null;
    }

    @Transactional
    @Override
    public Application processApplication(Long id, String newStatus) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada"));
        if (!List.of("PENDING", "ACCEPTED", "REJECTED").contains(newStatus)) {
            throw new RuntimeException("Status de candidatura inválido.");
        }
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }
}
