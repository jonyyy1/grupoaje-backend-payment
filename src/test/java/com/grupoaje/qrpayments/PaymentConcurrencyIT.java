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
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class PaymentConcurrencyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestRestTemplate rest;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void reset() {
        jdbc.update("DELETE FROM payment");
        jdbc.update("DELETE FROM account_balance");
        jdbc.update("""
      INSERT INTO account_balance(owner_type, owner_id, currency, balance, updated_at)
      VALUES ('USER','u1','PEN',100.00, now())
    """);
            jdbc.update("""
      INSERT INTO account_balance(owner_type, owner_id, currency, balance, updated_at)
      VALUES ('MERCHANT','m1','PEN',0.00, now())
    """);
    }

    @Test
    void concurrentPayments_doNotOverdraw() throws Exception {
        PaymentRequest p1 = new PaymentRequest("u1", new BigDecimal("80.00"), "PEN", "m1", "p-A");
        PaymentRequest p2 = new PaymentRequest("u1", new BigDecimal("80.00"), "PEN", "m1", "p-B");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        Future<PaymentResponse> f1 = pool.submit(() -> { start.await(); return rest.postForObject("/payment", p1, PaymentResponse.class); });
        Future<PaymentResponse> f2 = pool.submit(() -> { start.await(); return rest.postForObject("/payment", p2, PaymentResponse.class); });

        start.countDown();
        PaymentResponse r1 = f1.get(10, TimeUnit.SECONDS);
        PaymentResponse r2 = f2.get(10, TimeUnit.SECONDS);
        System.out.println("r1=" + r1);
        System.out.println("r2=" + r2);

        Integer ok = jdbc.queryForObject("SELECT COUNT(*) FROM payment WHERE status='SUCCESS'", Integer.class);
        Integer fail = jdbc.queryForObject("SELECT COUNT(*) FROM payment WHERE status='FAILED'", Integer.class);
        System.out.println("db success=" + ok + " failed=" + fail);
        pool.shutdownNow();

        assertThat(r1.status().equals("SUCCESS") || r2.status().equals("SUCCESS")).isTrue();
        assertThat(r1.status().equals("FAILED") || r2.status().equals("FAILED")).isTrue();

        BigDecimal userBal = jdbc.queryForObject("""
      SELECT balance FROM account_balance
      WHERE owner_type='USER' AND owner_id='u1' AND currency='PEN'
    """, BigDecimal.class);

        assertThat(userBal).isEqualByComparingTo("20.00");
    }
}

