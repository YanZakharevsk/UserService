package com.project.inno_online_store.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePaymentCardRequest {
    @NotBlank(message = "The name of holder can not be empty")
    @Pattern(regexp = "^[A-Z ]+$")
    @Size(max = 60, message = "The holder field must be shorter than 60 symbols")
    private String holder;

    @NotNull(message = "Date born can not be null")
    @Future(message = "The expiration date must be in the future")
    private LocalDate expirationDate;
}
