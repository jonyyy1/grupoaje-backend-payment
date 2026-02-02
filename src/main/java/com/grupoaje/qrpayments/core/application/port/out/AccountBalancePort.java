package com.grupoaje.qrpayments.core.application.port.out;

import com.grupoaje.qrpayments.core.domain.model.AccountBalance;
import com.grupoaje.qrpayments.core.domain.model.OwnerType;
import com.grupoaje.qrpayments.core.domain.money.CurrencyCode;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountBalancePort {

    Optional<AccountBalance> findForUpdate(OwnerType ownerType, String ownerId, CurrencyCode currency);

    void createIfMissing(OwnerType ownerType, String ownerId, CurrencyCode currency, BigDecimal initialBalance);

    void save(AccountBalance balance);
}
