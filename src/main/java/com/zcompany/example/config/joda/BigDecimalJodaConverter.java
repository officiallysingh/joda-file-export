package com.zcompany.example.config.joda;

import java.math.BigDecimal;

import org.joda.convert.TypedStringConverter;

import com.zcompany.example.domain.model.RateValue;

public class BigDecimalJodaConverter implements TypedStringConverter<BigDecimal> {

    @Override
    public String convertToString(final BigDecimal value) {
        return String.format("%." + RateValue.DECIMAL_POINTS_8 + "f", value);
    }

    @Override
    public BigDecimal convertFromString(final Class<? extends BigDecimal> cls, String str) {
        return new BigDecimal(str);
    }

    @Override
    public Class<?> getEffectiveType() {
        return BigDecimal.class;
    }
}
