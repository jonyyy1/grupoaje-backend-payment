package com.grupoaje.qrpayments.adapters.out.persistence.postgres;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.AccountBalanceEntity;
import com.grupoaje.qrpayments.adapters.out.persistence.postgres.mapper.BalanceMapper;
import com.grupoaje.qrpayments.adapters.out.persistence.postgres.repo.AccountBalanceJpaRepository;
import com.grupoaje.qrpayments.core.application.port.out.AccountBalancePort;
import com.grupoaje.qrpayments.core.domain.model.*;
import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class AccountBalancePostgresAdapter implements AccountBalancePort {

    private final AccountBalanceJpaRepository repo;

    public AccountBalancePostgresAdapter(AccountBalanceJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<AccountBalance> findForUpdate(OwnerType ownerType, String ownerId, CurrencyCode currency) {
        return repo.findForUpdate(ownerType.name(), ownerId, currency.code()).map(BalanceMapper::toDomain);
    }

    @Override
    public void createIfMissing(OwnerType ownerType, String ownerId, CurrencyCode currency, BigDecimal initialBalance) {
        repo.insertIfMissing(
                UUID.randomUUID(),
                ownerType.name(),
                ownerId,
                currency.code(),
                initialBalance,
                OffsetDateTime.now()
        );
    }

    @Override
    public void save(AccountBalance balance) {
        AccountBalanceEntity entity = repo.findForUpdate(
                balance.ownerType().name(),
                balance.ownerId(),
                balance.currency().code()
        ).orElseThrow(() -> new IllegalStateException("balance row not found"));

        BalanceMapper.apply(entity, balance);
        repo.save(entity);
    }
}

