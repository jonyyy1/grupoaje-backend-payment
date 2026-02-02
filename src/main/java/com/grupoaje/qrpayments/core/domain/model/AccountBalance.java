package com.grupoaje.qrpayments.core.domain.model;

import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class AccountBalance {
    private final OwnerType ownerType;
    private final String ownerId;
    private final CurrencyCode currency;

    private final BigDecimal balance;
    private final long version;
    private final OffsetDateTime updatedAt;

    public AccountBalance(
            OwnerType ownerType,
            String ownerId,
            CurrencyCode currency,
            BigDecimal balance,
            long version,
            OffsetDateTime updatedAt
    ) {
        this.ownerType = Objects.requireNonNull(ownerType);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.currency = Objects.requireNonNull(currency);
        this.balance = Objects.requireNonNull(balance);
        this.version = version;
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public OwnerType ownerType() { return ownerType; }
    public String ownerId() { return ownerId; }
    public CurrencyCode currency() { return currency; }
    public BigDecimal balance() { return balance; }

    public AccountBalance debit(BigDecimal amount) {
        return new AccountBalance(ownerType, ownerId, currency,
                balance.subtract(amount), version, OffsetDateTime.now());
    }

    public AccountBalance credit(BigDecimal amount) {
        return new AccountBalance(ownerType, ownerId, currency,
                balance.add(amount), version, OffsetDateTime.now());
    }
}

