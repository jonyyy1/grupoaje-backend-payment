package com.grupoaje.qrpayments;

import com.grupoaje.qrpayments.adapters.in.rest.dto.PaymentRequest;
import com.grupoaje.qrpayments.adapters.in.rest.dto.PaymentResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class PaymentIdempotencyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestRestTemplate rest;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void reset() {
        jdbc.update("DELETE FROM payment");
        jdbc.update("DELETE FROM account_balance");
    }

    @Test
    void samePaymentId_isIdempotent() {
        PaymentRequest req = new PaymentRequest("u1", new BigDecimal("10.00"), "PEN", "m1", "p-123");

        PaymentResponse r1 = rest.postForObject("/payment", req, PaymentResponse.class);
        PaymentResponse r2 = rest.postForObject("/payment", req, PaymentResponse.class);

        assertThat(r1.status()).isEqualTo("SUCCESS");
        assertThat(r2.status()).isEqualTo("SUCCESS");
        assertThat(r1.user_balance_after()).isEqualByComparingTo("90.00");
        assertThat(r2.user_balance_after()).isEqualByComparingTo("90.00");

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM payment WHERE payment_id = 'p-123'", Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
