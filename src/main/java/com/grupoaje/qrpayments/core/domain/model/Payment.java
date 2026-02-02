package com.grupoaje.qrpayments.core.domain.model;

import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class Payment {
    private final String paymentId;
    private final String requestHash;
    private final String userId;
    private final String merchantId;
    private final CurrencyCode currency;
    private final BigDecimal amount;

    private final PaymentStatus status;
    private final String errorCode;
    private final BigDecimal userBalanceAfter;
    private final BigDecimal merchantBalanceAfter;

    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Payment(
            String paymentId,
            String requestHash,
            String userId,
            String merchantId,
            CurrencyCode currency,
            BigDecimal amount,
            PaymentStatus status,
            String errorCode,
            BigDecimal userBalanceAfter,
            BigDecimal merchantBalanceAfter,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.paymentId = require(paymentId, "paymentId");
        this.requestHash = require(requestHash, "requestHash");
        this.userId = require(userId, "userId");
        this.merchantId = require(merchantId, "merchantId");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.status = Objects.requireNonNull(status, "status");
        this.errorCode = errorCode;
        this.userBalanceAfter = userBalanceAfter;
        this.merchantBalanceAfter = merchantBalanceAfter;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    private static String require(String v, String n) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " required");
        return v;
    }

    public String paymentId() { return paymentId; }
    public String requestHash() { return requestHash; }
    public PaymentStatus status() { return status; }
    public String errorCode() { return errorCode; }
    public BigDecimal userBalanceAfter() { return userBalanceAfter; }
    public BigDecimal merchantBalanceAfter() { return merchantBalanceAfter; }
}
