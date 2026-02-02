package com.grupoaje.qrpayments.adapters.out.persistence.postgres;

import com.grupoaje.qrpayments.adapters.out.persistence.postgres.entity.PaymentEntity;
import com.grupoaje.qrpayments.adapters.out.persistence.postgres.mapper.PaymentMapper;
import com.grupoaje.qrpayments.adapters.out.persistence.postgres.repo.PaymentJpaRepository;
import com.grupoaje.qrpayments.core.application.port.out.PaymentIntent;
import com.grupoaje.qrpayments.core.application.port.out.PaymentStorePort;
import com.grupoaje.qrpayments.core.domain.model.Payment;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PaymentStorePostgresAdapter implements PaymentStorePort {

    private final PaymentJpaRepository repo;

    public PaymentStorePostgresAdapter(PaymentJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean tryCreatePaymentIntent(PaymentIntent intent) {
        int inserted = repo.insertIntentOnConflictDoNothing(
                intent.id(),
                intent.paymentId(),
                intent.requestHash(),
                intent.userId(),
                intent.merchantId(),
                intent.currency().code(),
                intent.amount(),
                intent.now()
        );
        return inserted == 1;
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return repo.findByPaymentId(paymentId).map(PaymentMapper::toDomain);
    }

    @Override
    public void saveFinal(Payment payment) {
        PaymentEntity entity = repo.findByPaymentId(payment.paymentId())
                .orElseThrow(() -> new IllegalStateException("payment row not found"));
        PaymentMapper.applyFinal(entity, payment);
        repo.save(entity);
    }
}

