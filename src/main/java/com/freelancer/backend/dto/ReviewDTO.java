package com.freelancer.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;

    @NotNull
    private Long projectId;

    @NotNull
    private Long reviewedId; // id do usuário sendo avaliado

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;
}
