package com.grupoaje.qrpayments.core.domain.money;

public record CurrencyCode(String code) {
    public CurrencyCode {
        if (code == null || !code.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("currency must be ISO-4217 like 'PEN' or 'USD'");
        }
    }
}
