package com.dash.telegram.service;

import com.dash.telegram.entity.Currency;
import com.dash.telegram.service.impl.NbrbCurrencyConversionService;

public interface CurrencyConversionService {

    static CurrencyConversionService getInstance() { return new NbrbCurrencyConversionService(); }

    double getConversionRatio(Currency original, Currency target);
}
