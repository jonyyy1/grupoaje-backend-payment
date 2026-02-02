package com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_balance", uniqueConstraints = {
        @UniqueConstraint(name = "uq_balance_owner_currency", columnNames = {"owner_type","owner_id","currency"})
})
public class AccountBalanceEntity {

    @Id
    public UUID id;

    @Column(name="owner_type", nullable=false, length=16)
    public String ownerType;

    @Column(name="owner_id", nullable=false, length=64)
    public String ownerId;

    @Column(nullable=false, length=3)
    public String currency;

    @Column(nullable=false, precision=19, scale=2)
    public BigDecimal balance;

    @Version
    @Column(nullable=false)
    public long version;

    @Column(name="updated_at", nullable=false)
    public OffsetDateTime updatedAt;
}

