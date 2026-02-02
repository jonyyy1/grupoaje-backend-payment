package com.grupoaje.qrpayments.core.application.port.in;

import com.grupoaje.qrpayments.core.application.dto.ProcessPaymentCommand;
import com.grupoaje.qrpayments.core.application.dto.PaymentResult;

public interface ProcessPaymentUseCase {
    PaymentResult process(ProcessPaymentCommand cmd);
}
