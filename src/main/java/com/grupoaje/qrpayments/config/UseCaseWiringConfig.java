package com.grupoaje.qrpayments.config;


import com.grupoaje.qrpayments.core.application.port.in.ProcessPaymentUseCase;
import com.grupoaje.qrpayments.core.application.port.out.*;
import com.grupoaje.qrpayments.core.application.service.ProcessPaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class UseCaseWiringConfig {

    @Bean
    ProcessPaymentUseCase processPaymentUseCase(
            PaymentStorePort paymentStorePort,
            AccountBalancePort accountBalancePort,
            ClockPort clockPort,
            TransactionTemplate txTemplate
    ) {
        ProcessPaymentService core = new ProcessPaymentService(paymentStorePort, accountBalancePort, clockPort);

        return cmd -> txTemplate.execute(status -> core.process(cmd));
    }
}

