package com.grupoaje.qrpayments.core.application.dto;

import java.math.BigDecimal;

public record ProcessPaymentCommand(
        String userId,
        BigDecimal amount,
        String currency,
        String merchantId,
        String paymentId
) {}
