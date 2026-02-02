package com.grupoaje.qrpayments.adapters.out.persistence.postgres.mapper;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.AccountBalanceEntity;
import com.grupoaje.qrpayments.core.domain.model.AccountBalance;
import com.grupoaje.qrpayments.core.domain.model.OwnerType;
import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;

public final class BalanceMapper {

    private BalanceMapper() {}

    public static AccountBalance toDomain(AccountBalanceEntity e) {
        return new AccountBalance(
                OwnerType.valueOf(e.ownerType),
                e.ownerId,
                new CurrencyCode(e.currency),
                e.balance,
                e.version,
                e.updatedAt
        );
    }

    public static void apply(AccountBalanceEntity e, AccountBalance d) {
        e.balance = d.balance();
        e.updatedAt = java.time.OffsetDateTime.now();
    }
}

