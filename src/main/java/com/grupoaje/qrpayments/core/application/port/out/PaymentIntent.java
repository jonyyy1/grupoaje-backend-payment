package com.grupoaje.qrpayments.core.application.port.out;

import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentIntent(
        UUID id,
        String paymentId,
        String requestHash,
        String userId,
        String merchantId,
        CurrencyCode currency,
        BigDecimal amount,
        OffsetDateTime now
) {}