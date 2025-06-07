package com.freelancer.backend.service.impl;

import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Review;
import com.freelancer.backend.model.User;
import com.freelancer.backend.repository.ReviewRepository;
import com.freelancer.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Review createReview(User reviewer, User reviewed, Project project, int rating, String comment) {
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .project(project)
                .rating(rating)
                .comment(comment)
                .build();
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByReviewed(User reviewed) {
        return reviewRepository.findByReviewed(reviewed);
    }

    @Override
    public List<Review> getReviewsByProject(Project project) {
        return reviewRepository.findByProject(project);
    }
}
