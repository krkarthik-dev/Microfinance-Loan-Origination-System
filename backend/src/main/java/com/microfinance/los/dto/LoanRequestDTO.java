package com.microfinance.los.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class LoanRequestDTO {
    
    // For submission request
    @NotNull
    @Min(100)
    private BigDecimal principalAmount;

    @NotNull
    @Min(1)
    private Integer tenureMonths;

    @NotNull
    private Integer age;

    @NotNull
    private Integer dependents;

    @NotBlank
    private String geographicalLocation;

    @NotNull
    private BigDecimal declaredIncome;

    // Optional fields when used as Kafka Payload to also pass the generated IDs
    private UUID loanId;
    private UUID userId;
}
