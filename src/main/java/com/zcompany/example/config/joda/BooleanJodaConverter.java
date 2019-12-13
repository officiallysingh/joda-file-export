package com.zcompany.example.config.joda;

import java.math.BigDecimal;

import org.joda.convert.TypedStringConverter;

public class BooleanJodaConverter implements TypedStringConverter<Boolean> {

    @Override
    public String convertToString(final Boolean value) {
        return value.booleanValue() ? "Enabled" : "Disabled";
    }

    @Override
    public Boolean convertFromString(final Class<? extends Boolean> cls, String str) {
        return str.equals("Enabled") ? true : false;
    }

    @Override
    public Class<?> getEffectiveType() {
        return BigDecimal.class;
    }
}
