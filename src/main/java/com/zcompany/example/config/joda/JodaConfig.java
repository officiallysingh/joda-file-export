package com.zcompany.example.config.joda;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.money.CurrencyUnit;

import org.joda.convert.StringConvert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JodaConfig {

    @Bean
    public StringConvert jodaConverter() {
        StringConvert stringConvert = StringConvert.create();
        stringConvert.register(BigDecimal.class, new BigDecimalJodaConverter());
        stringConvert.register(Boolean.class, new BooleanJodaConverter());
        stringConvert.register(CurrencyUnit.class, new CurrencyUnitJodaConverter());
        stringConvert.register(ZonedDateTime.class, new ZonedDateTimeJodaConverter());
        return stringConvert;
    }
}
