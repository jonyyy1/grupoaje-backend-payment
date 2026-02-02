package com.grupoaje.qrpayments.adapters.out.persistence.postgres.repo;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.AccountBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AccountBalanceJpaRepository extends JpaRepository<AccountBalanceEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select b from AccountBalanceEntity b
    where b.ownerType = :ownerType
      and b.ownerId = :ownerId
      and b.currency = :currency
  """)
    Optional<AccountBalanceEntity> findForUpdate(
            @Param("ownerType") String ownerType,
            @Param("ownerId") String ownerId,
            @Param("currency") String currency
    );

    @Modifying
    @Query(value = """
    INSERT INTO account_balance(id, owner_type, owner_id, currency, balance, updated_at)
    VALUES (:id, :ownerType, :ownerId, :currency, :balance, :now)
    ON CONFLICT (owner_type, owner_id, currency) DO NOTHING
  """, nativeQuery = true)
    int insertIfMissing(
            @Param("id") UUID id,
            @Param("ownerType") String ownerType,
            @Param("ownerId") String ownerId,
            @Param("currency") String currency,
            @Param("balance") BigDecimal balance,
            @Param("now") OffsetDateTime now
    );
}

