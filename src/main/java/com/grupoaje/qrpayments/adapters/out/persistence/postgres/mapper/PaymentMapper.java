package com.grupoaje.qrpayments.adapters.out.persistence.postgres.mapper;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.PaymentEntity;
import com.grupoaje.qrpayments.core.domain.model.*;
import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static Payment toDomain(PaymentEntity e) {
        return new Payment(
                e.paymentId, e.requestHash, e.userId, e.merchantId,
                new CurrencyCode(e.currency), e.amount,
                PaymentStatus.valueOf(e.status),
                e.errorCode,
                e.userBalanceAfter,
                e.merchantBalanceAfter,
                e.createdAt, e.updatedAt
        );
    }

    public static void applyFinal(PaymentEntity e, Payment d) {
        e.status = d.status().name();
        e.errorCode = d.errorCode();
        e.userBalanceAfter = d.userBalanceAfter();
        e.merchantBalanceAfter = d.merchantBalanceAfter();
        e.updatedAt = java.time.OffsetDateTime.now();
    }
}

