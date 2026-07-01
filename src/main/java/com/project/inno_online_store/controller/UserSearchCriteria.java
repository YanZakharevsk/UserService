package com.project.inno_online_store.controller;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserSearchCriteria {
    private String name;
    private String surname;
    @NotNull(message = "Page field can not null")
    @Min(value = 0,message = "Page number can not less than 0")
    private int page;
    @NotNull(message = "Size field can not null")
    @Min(value = 1, message = "Size number can not less than 1")
    @Max(value = 100, message = "Size number can not more than 100")
    private int size;
}
