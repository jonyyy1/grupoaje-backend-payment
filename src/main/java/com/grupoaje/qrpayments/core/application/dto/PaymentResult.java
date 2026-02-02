package com.grupoaje.qrpayments.core.application.dto;

import java.math.BigDecimal;

public record PaymentResult(
        String paymentId,
        String status,
        String errorCode,
        BigDecimal userBalanceAfter,
        BigDecimal merchantBalanceAfter
) {}
