package com.freelancer.backend.repository;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Review;
import com.freelancer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewed(User reviewed);
    List<Review> findByReviewer(User reviewer);
    List<Review> findByProject(Project project);
}
