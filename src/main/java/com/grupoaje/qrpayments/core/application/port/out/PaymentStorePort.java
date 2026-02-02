package com.grupoaje.qrpayments.core.application.port.out;

import com.grupoaje.qrpayments.core.domain.model.Payment;
import java.util.Optional;

public interface PaymentStorePort {

    boolean tryCreatePaymentIntent(PaymentIntent intent);

    Optional<Payment> findByPaymentId(String paymentId);

    void saveFinal(Payment payment);
}
