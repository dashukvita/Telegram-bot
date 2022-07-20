package com.dash.telegram.service.impl;


import com.dash.telegram.entity.Currency;
import com.dash.telegram.service.CurrencyModeService;

import java.util.HashMap;
import java.util.Map;

public class HashMapCurrencyModeService implements CurrencyModeService {
    private final Map<Long, Currency> originalCurrency = new HashMap<>();
    private final Map<Long, Currency> targetCurrency = new HashMap<>();


    @Override
    public Currency getOriginalCurrency(long chatId) {
        return originalCurrency.getOrDefault(chatId, Currency.USD);
    }

    @Override
    public Currency getTargetCurrency(long chatId) {
        return targetCurrency.getOrDefault(chatId, Currency.USD);
    }

    @Override
    public void setOriginalCurrency(long chartId, Currency currency) {
        originalCurrency.put(chartId, currency);
    }

    @Override
    public void setTargetCurrency(long chatId, Currency currency) {
        targetCurrency.put(chatId, currency);
    }

}
