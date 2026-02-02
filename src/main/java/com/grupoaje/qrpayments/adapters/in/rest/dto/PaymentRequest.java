package com.grupoaje.qrpayments.adapters.in.rest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String user_id,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @NotBlank String merchant_id,
        @NotBlank String payment_id
) {}