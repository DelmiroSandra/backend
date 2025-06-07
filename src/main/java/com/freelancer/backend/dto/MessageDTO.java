package com.freelancer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Long receiverId;

    private Long projectId; // opcional
}
