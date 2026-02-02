package com.grupoaje.qrpayments.adapters.in.rest.error;

import com.grupoaje.qrpayments.core.application.error.IdempotencyConflictException;
import com.grupoaje.qrpayments.core.application.error.InsufficientFundsException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ProblemDetail> insufficient(InsufficientFundsException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.OK);
        pd.setTitle("Pago procesado con fallo");
        pd.setDetail(ex.getMessage());
        pd.setProperty("error_code", "INSUFFICIENT_FUNDS");
        return ResponseEntity.ok(pd);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ProblemDetail> idempotencyConflict(IdempotencyConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflicto de idempotencia");
        pd.setDetail(ex.getMessage());
        pd.setProperty("error_code", "PAYMENT_ID_REUSE");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }
}
