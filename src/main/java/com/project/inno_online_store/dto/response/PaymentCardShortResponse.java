package com.project.inno_online_store.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardShortResponse {
    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean isActive;
}
