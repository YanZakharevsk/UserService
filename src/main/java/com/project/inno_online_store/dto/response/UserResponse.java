package com.project.inno_online_store.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean isActive;
    private List<PaymentCardShortResponse> paymentCards;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
