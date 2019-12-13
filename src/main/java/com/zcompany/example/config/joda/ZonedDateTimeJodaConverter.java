package com.zcompany.example.config.joda;

import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;

import org.joda.convert.TypedStringConverter;

import com.zcompany.example.util.DateTimeHelper;

public class ZonedDateTimeJodaConverter implements TypedStringConverter<ZonedDateTime> {

    @Override
    public String convertToString(ZonedDateTime object) {
        return DateTimeHelper.convertAndFormatZonedDateTime(object);
    }

    @Override
    public ZonedDateTime convertFromString(Class<? extends ZonedDateTime> cls, String str) {
        throw new UnsupportedOperationException("Not required yet");
    }

    @Override
    public Class<?> getEffectiveType() {
        return CurrencyUnit.class;
    }
}
