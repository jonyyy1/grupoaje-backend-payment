package com.grupoaje.qrpayments.adapters.in.rest;

import com.grupoaje.qrpayments.adapters.in.rest.dto.*;
import com.grupoaje.qrpayments.core.application.dto.ProcessPaymentCommand;
import com.grupoaje.qrpayments.core.application.dto.PaymentResult;
import com.grupoaje.qrpayments.core.application.port.in.ProcessPaymentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class PaymentController {

    private final ProcessPaymentUseCase useCase;

    public PaymentController(ProcessPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentRequest req) {
        PaymentResult result = useCase.process(new ProcessPaymentCommand(
                req.user_id(), req.amount(), req.currency(), req.merchant_id(), req.payment_id()
        ));

        return ResponseEntity.ok(new PaymentResponse(
                result.paymentId(),
                result.status(),
                result.errorCode(),
                result.userBalanceAfter(),
                result.merchantBalanceAfter()
        ));
    }
}
