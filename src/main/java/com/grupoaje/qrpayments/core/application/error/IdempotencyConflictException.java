package com.grupoaje.qrpayments.core.application.error;

public class IdempotencyConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public IdempotencyConflictException(String message) { super(message); }
    public IdempotencyConflictException(String message, Throwable cause) { super(message, cause); }
}

