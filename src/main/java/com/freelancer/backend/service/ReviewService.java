package com.freelancer.backend.service;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Review;
import com.freelancer.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(User reviewer, User reviewed, Project project, int rating, String comment);
    Optional<Review> getReviewById(Long id);
    List<Review> getReviewsByReviewed(User reviewed);
    List<Review> getReviewsByProject(Project project);
}
