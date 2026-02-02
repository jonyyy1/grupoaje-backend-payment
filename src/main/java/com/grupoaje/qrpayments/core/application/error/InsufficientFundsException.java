package com.grupoaje.qrpayments.core.application.error;

public class InsufficientFundsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InsufficientFundsException(String message) { super(message); }
    public InsufficientFundsException(String message, Throwable cause) { super(message, cause); }
}
