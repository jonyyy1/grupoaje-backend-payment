package com.grupoaje.qrpayments.adapters.out.clock;


import com.grupoaje.qrpayments.core.application.port.out.ClockPort;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class SystemClockAdapter implements ClockPort {

    private final Clock clock;

    public SystemClockAdapter() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now(clock);
    }
}