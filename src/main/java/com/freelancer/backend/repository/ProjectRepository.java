package com.freelancer.backend.repository;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Listar projetos abertos
    List<Project> findByStatus(String status);
    // Listar projetos de um contratante específico
    List<Project> findByContractor(User contractor);
}
