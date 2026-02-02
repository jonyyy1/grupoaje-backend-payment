package com.grupoaje.qrpayments;

import org.springframework.boot.SpringApplication;

public class TestQrPaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.from(QrPaymentsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
