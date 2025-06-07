package com.freelancer.backend.repository;

import com.freelancer.backend.model.Application;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByProject(Project project);
    List<Application> findByFreelancer(User freelancer);
    Optional<Application> findByFreelancerAndProject(User freelancer, Project project);
}
