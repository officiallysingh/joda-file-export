package com.zcompany.example.config.joda;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.joda.convert.TypedStringConverter;

public class CurrencyUnitJodaConverter implements TypedStringConverter<CurrencyUnit> {

    @Override
    public String convertToString(CurrencyUnit object) {
        return object.getCurrencyCode();
    }

    @Override
    public CurrencyUnit convertFromString(Class<? extends CurrencyUnit> cls, String str) {
        return Monetary.getCurrency(str);
    }

    @Override
    public Class<?> getEffectiveType() {
        return CurrencyUnit.class;
    }
}
