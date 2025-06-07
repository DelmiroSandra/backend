package com.freelancer.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;

    @NotNull
    private Long projectId;

    @NotNull
    private Long receiverId;

    @NotNull
    private Double amount;
}
