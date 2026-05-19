package com.project.inno_online_store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Name field can not be empty")
    @Size(max = 100, message = "Name field must be shorter than 100 symbols")
    private String name;

    @NotBlank(message = "Surname field can not be empty")
    @Size(max = 100, message = "Surname field must be shorter than 100 symbols")
    private String surname;
}
