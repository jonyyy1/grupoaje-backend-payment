package com.grupoaje.qrpayments.adapters.out.persistence.postgres.repo;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.PaymentEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByPaymentId(String paymentId);

    @Modifying
    @Query(value = """
        INSERT INTO payment(
          id, payment_id, request_hash, user_id, merchant_id, currency, amount,
          status, error_code, user_balance_after, merchant_balance_after,
          created_at, updated_at
        )
        VALUES (
          :id, :paymentId, :requestHash, :userId, :merchantId, :currency, :amount,
          'FAILED', NULL, NULL, NULL,
          :now, :now
        )
        ON CONFLICT (payment_id) DO NOTHING
      """, nativeQuery = true)
    int insertIntentOnConflictDoNothing(
            @Param("id") UUID id,
            @Param("paymentId") String paymentId,
            @Param("requestHash") String requestHash,
            @Param("userId") String userId,
            @Param("merchantId") String merchantId,
            @Param("currency") String currency,
            @Param("amount") BigDecimal amount,
            @Param("now") OffsetDateTime now
    );
}

