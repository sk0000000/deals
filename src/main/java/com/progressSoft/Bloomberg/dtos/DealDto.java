package com.progressSoft.Bloomberg.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DealDto {

    private String dealUniqueId;

    private LocalDateTime dealTimestamp;

    @NotBlank(message = "fromCurrency is mandatory")
    @Size(min = 3, max = 3, message = "fromCurrency must be exactly 3 letters")
    private String fromCurrency;

    @NotBlank(message = "toCurrency is mandatory")
    @Size(min = 3, max = 3, message = "toCurrency must be exactly 3 letters")
    private String toCurrency;

    @NotNull(message = "dealAmount is mandatory")
    @DecimalMin(value = "0.01", inclusive = true, message = "dealAmount must be positive")
    private BigDecimal dealAmount;
}
