package com.grupoaje.qrpayments.core.application.service;


import com.grupoaje.qrpayments.core.application.dto.ProcessPaymentCommand;
import com.grupoaje.qrpayments.core.application.dto.PaymentResult;
import com.grupoaje.qrpayments.core.application.error.IdempotencyConflictException;
import com.grupoaje.qrpayments.core.application.port.in.ProcessPaymentUseCase;
import com.grupoaje.qrpayments.core.application.port.out.*;
import com.grupoaje.qrpayments.core.domain.model.*;
import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

public final class ProcessPaymentService implements ProcessPaymentUseCase {

    private final PaymentStorePort paymentStore;
    private final AccountBalancePort balances;
    private final ClockPort clock;

    public ProcessPaymentService(PaymentStorePort paymentStore, AccountBalancePort balances, ClockPort clock) {
        this.paymentStore = paymentStore;
        this.balances = balances;
        this.clock = clock;
    }

    @Override
    public PaymentResult process(ProcessPaymentCommand cmd) {
        CurrencyCode currency = new CurrencyCode(cmd.currency());
        BigDecimal amount = cmd.amount();

        String requestHash = sha256(cmd.userId(), cmd.merchantId(), cmd.currency(), amount, cmd.paymentId());

        OffsetDateTime now = clock.now();
        PaymentIntent intent = new PaymentIntent(
                UUID.randomUUID(),
                cmd.paymentId(),
                requestHash,
                cmd.userId(),
                cmd.merchantId(),
                currency,
                amount,
                now
        );

        boolean imOwner = paymentStore.tryCreatePaymentIntent(intent);

        if (!imOwner) {
            Payment existing = paymentStore.findByPaymentId(cmd.paymentId())
                    .orElseThrow(() -> new IllegalStateException("payment_id existe pero no se pudo leer"));

            if (!existing.requestHash().equals(requestHash)) {
                throw new IdempotencyConflictException("payment_id reutilizado con payload distinto");
            }

            return new PaymentResult(
                    existing.paymentId(),
                    existing.status().name(),
                    existing.errorCode(),
                    existing.userBalanceAfter(),
                    existing.merchantBalanceAfter()
            );
        }

        balances.createIfMissing(OwnerType.USER, cmd.userId(), currency, new BigDecimal("100.00"));
        balances.createIfMissing(OwnerType.MERCHANT, cmd.merchantId(), currency, BigDecimal.ZERO);

        AccountBalance userBal = balances.findForUpdate(OwnerType.USER, cmd.userId(), currency)
                .orElseThrow(() -> new IllegalStateException("user balance missing"));
        AccountBalance merchantBal = balances.findForUpdate(OwnerType.MERCHANT, cmd.merchantId(), currency)
                .orElseThrow(() -> new IllegalStateException("merchant balance missing"));

        if (userBal.balance().compareTo(amount) < 0) {
            Payment failed = new Payment(
                    cmd.paymentId(), requestHash, cmd.userId(), cmd.merchantId(), currency, amount,
                    PaymentStatus.FAILED, "INSUFFICIENT_FUNDS",
                    userBal.balance(), merchantBal.balance(),
                    now, now
            );
            paymentStore.saveFinal(failed);

            return new PaymentResult(
                    failed.paymentId(),
                    PaymentStatus.FAILED.name(),
                    failed.errorCode(),
                    failed.userBalanceAfter(),
                    failed.merchantBalanceAfter()
            );
        }

        AccountBalance userAfter = userBal.debit(amount);
        AccountBalance merchantAfter = merchantBal.credit(amount);

        balances.save(userAfter);
        balances.save(merchantAfter);

        Payment success = new Payment(
                cmd.paymentId(), requestHash, cmd.userId(), cmd.merchantId(), currency, amount,
                PaymentStatus.SUCCESS, null,
                userAfter.balance(), merchantAfter.balance(),
                now, now
        );
        paymentStore.saveFinal(success);

        return new PaymentResult(
                success.paymentId(),
                "SUCCESS",
                null,
                success.userBalanceAfter(),
                success.merchantBalanceAfter()
        );
    }

    private static String sha256(String userId, String merchantId, String currency, BigDecimal amount, String paymentId) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String payload = userId + "|" + merchantId + "|" + currency + "|" + amount.toPlainString() + "|" + paymentId;
            return HexFormat.of().formatHex(md.digest(payload.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

