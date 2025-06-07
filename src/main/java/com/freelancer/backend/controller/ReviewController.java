package com.freelancer.backend.controller;

import com.freelancer.backend.dto.ApiResponse;
import com.freelancer.backend.dto.ReviewDTO;
import com.freelancer.backend.model.Project;
import com.freelancer.backend.model.Review;
import com.freelancer.backend.model.User;
import com.freelancer.backend.service.ProjectService;
import com.freelancer.backend.service.ReviewService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    private final ModelMapper modelMapper = new ModelMapper();

    // POST /api/reviews → criar avaliação
    @PostMapping
    public ResponseEntity<?> createReview(Authentication authentication,
                                          @Valid @RequestBody ReviewDTO reviewDTO) {
        String username = authentication.getName();
        User reviewer = userService.findByUsername(username).orElseThrow();
        User reviewed = userService.findById(reviewDTO.getReviewedId())
                .orElseThrow(() -> new RuntimeException("Usuário a ser avaliado não encontrado"));
        Project project = projectService.getProjectById(reviewDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        // Aqui você pode adicionar checagem: só permite avaliar se projeto finalizado e usuário participou.
        Review review = reviewService.createReview(
                reviewer,
                reviewed,
                project,
                reviewDTO.getRating(),
                reviewDTO.getComment()
        );
        return ResponseEntity.ok(modelMapper.map(review, ReviewDTO.class));
    }

    // GET /api/reviews/user/{userId} → lista avaliações recebidas por usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable Long userId) {
        User reviewed = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Review> reviews = reviewService.getReviewsByReviewed(reviewed);
        List<ReviewDTO> result = reviews.stream()
                .map(r -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setId(r.getId());
                    dto.setProjectId(r.getProject().getId());
                    dto.setReviewedId(r.getReviewed().getId());
                    dto.setRating(r.getRating());
                    dto.setComment(r.getComment());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // GET /api/reviews/project/{projectId} → lista avaliações de um projeto
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getReviewsByProject(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        List<Review> reviews = reviewService.getReviewsByProject(project);
        List<ReviewDTO> result = reviews.stream()
                .map(r -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setId(r.getId());
                    dto.setProjectId(r.getProject().getId());
                    dto.setReviewedId(r.getReviewed().getId());
                    dto.setRating(r.getRating());
                    dto.setComment(r.getComment());
                    return dto;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
