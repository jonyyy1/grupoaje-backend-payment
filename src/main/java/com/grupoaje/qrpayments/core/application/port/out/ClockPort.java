package com.grupoaje.qrpayments.core.application.port.out;

import java.time.OffsetDateTime;

public interface ClockPort {
    OffsetDateTime now();
}
