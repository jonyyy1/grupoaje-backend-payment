package com.grupoaje.qrpayments.adapters.in.rest.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String payment_id,
        String status,
        String error_code,
        BigDecimal user_balance_after,
        BigDecimal merchant_balance_after
) {}
