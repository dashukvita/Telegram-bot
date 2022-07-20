package com.dash.telegram.service;

import com.dash.telegram.entity.Currency;
import com.dash.telegram.service.impl.HashMapCurrencyModeService;


public interface CurrencyModeService {

    static CurrencyModeService getInstance() { return new HashMapCurrencyModeService();}

    Currency getOriginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chartId, Currency currency);

    void setTargetCurrency(long chatId, Currency currency);
}
