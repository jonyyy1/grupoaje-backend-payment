package com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(name = "uq_payment_payment_id", columnNames = "payment_id")
})
public class PaymentEntity {

    @Id
    public UUID id;

    @Column(name = "payment_id", nullable = false, length = 64)
    public String paymentId;

    @Column(name = "request_hash", nullable = false, length = 64)
    public String requestHash;

    @Column(name = "user_id", nullable = false, length = 64)
    public String userId;

    @Column(name = "merchant_id", nullable = false, length = 64)
    public String merchantId;

    @Column(nullable = false, length = 3)
    public String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    public BigDecimal amount;

    @Column(nullable = false, length = 16)
    public String status;

    @Column(name = "error_code", length = 64)
    public String errorCode;

    @Column(name = "user_balance_after", precision = 19, scale = 2)
    public BigDecimal userBalanceAfter;

    @Column(name = "merchant_balance_after", precision = 19, scale = 2)
    public BigDecimal merchantBalanceAfter;

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;
}
