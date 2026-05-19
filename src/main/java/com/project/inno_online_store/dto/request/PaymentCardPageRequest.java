package com.project.inno_online_store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCardPageRequest {
    @NotNull(message = "Page field can not null")
    @Min(value = 0,message = "Page number can not less than 0")
    private int page;
    @NotNull(message = "Size field can not null")
    @Min(value = 1, message = "Size number can not less than 1")
    @Max(value = 100, message = "Size number can not more than 100")
    private int size;
}
