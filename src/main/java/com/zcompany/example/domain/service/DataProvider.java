package com.zcompany.example.domain.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zcompany.example.domain.model.Cost;
import com.zcompany.example.domain.model.CurrencyExchange;
import com.zcompany.example.domain.model.Exchange;
import com.zcompany.example.domain.model.ExchangeRate;
import com.zcompany.example.domain.model.InterBankRate;
import com.zcompany.example.domain.model.RateValue;
import com.zcompany.example.domain.model.Timesheet;
import com.zcompany.example.domain.model.VDWType;
import com.zcompany.example.domain.model.ValueAtRisk;
import com.zcompany.example.domain.model.ValueDateWise;
import com.zcompany.example.util.CurrencyCode;

public class DataProvider {

    public static List<InterBankRate> getInterBankRates() {
        List<InterBankRate> data = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            if (i < 3) {
                data.add(InterBankRate.of("abc" + i,
                        Exchange.of(CurrencyExchange.of(CurrencyCode.INR.getCurrencyUnit(),
                                CurrencyCode.USD.getCurrencyUnit()), ExchangeRate.zero()),
                        "TRAVELLER-" + i, "HDFC" + i, ZonedDateTime.now(), Boolean.TRUE));
            } else if (i > 2 && i < 7) {
                data.add(InterBankRate.of("xyz" + i,
                        Exchange.of(CurrencyExchange.of(CurrencyCode.GBP.getCurrencyUnit(),
                                CurrencyCode.USD.getCurrencyUnit()), ExchangeRate.one()),
                        "INTERNATIONAL-" + i, "HSBC" + i, ZonedDateTime.now(), Boolean.FALSE));
            } else {
                data.add(InterBankRate.of("jhq" + i, Exchange.of(
                        CurrencyExchange.of(CurrencyCode.GBP.getCurrencyUnit(), CurrencyCode.AED.getCurrencyUnit()),
                        ExchangeRate.of(RateValue.of(15.73).multiply(RateValue.of(i)),
                                new BigDecimal(16.17).multiply(new BigDecimal(i)))),
                        "AGENT_SMITH-" + i, null, null, Boolean.TRUE));
            }
        }
        return data;
    }

    public static List<ValueAtRisk> getValueAtRiskRates() {
        List<ValueAtRisk> data = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            if (i < 3) {
                Map<VDWType, ValueDateWise> valueDateWises = VDWType.all().stream()
                        .collect(Collectors.toMap(vdw -> vdw,
                                vdw -> ValueDateWise.of(Cost.of(RateValue.ZERO, RateValue.ONE),
                                        Cost.of(RateValue.ONE, RateValue.ZERO)),
                                (e1, e2) -> e1, () -> new LinkedHashMap<VDWType, ValueDateWise>(4)));
                data.add(ValueAtRisk.of("asdf" + i,
                        Exchange.of(CurrencyExchange.of(CurrencyCode.INR.getCurrencyUnit(),
                                CurrencyCode.USD.getCurrencyUnit()), ExchangeRate.zero()),
                        "HDFC-" + i, "TRAVELLER-" + i, valueDateWises, ZonedDateTime.now(), Boolean.TRUE));
            } else if (i > 2 && i < 7) {
                Map<VDWType, ValueDateWise> valueDateWises = VDWType.all().stream()
                        .collect(Collectors.toMap(vdw -> vdw,
                                vdw -> ValueDateWise.of(Cost.of(RateValue.of(123.34), RateValue.of(430.23)),
                                        Cost.of(RateValue.of(106.05), RateValue.of(203.98))),
                                (e1, e2) -> e1, () -> new LinkedHashMap<VDWType, ValueDateWise>(4)));
                data.add(ValueAtRisk.of("asdf" + i,
                        Exchange.of(CurrencyExchange.of(CurrencyCode.GBP.getCurrencyUnit(),
                                CurrencyCode.AED.getCurrencyUnit()), ExchangeRate.one()),
                        "HSBC-" + i, "INTERNATIONAL-" + i, valueDateWises, ZonedDateTime.now(), Boolean.TRUE));
            } else {
                Map<VDWType, ValueDateWise> valueDateWises = VDWType.all().stream()
                        .collect(Collectors.toMap(vdw -> vdw,
                                vdw -> ValueDateWise.of(Cost.of(RateValue.of(123.34), RateValue.of(430.23)),
                                        Cost.of(RateValue.of(88.99), RateValue.of(100.200))),
                                (e1, e2) -> e1, () -> new LinkedHashMap<VDWType, ValueDateWise>(4)));
                data.add(ValueAtRisk.of("asdf" + i,
                        Exchange.of(
                                CurrencyExchange.of(CurrencyCode.INR.getCurrencyUnit(),
                                        CurrencyCode.PKR.getCurrencyUnit()),
                                ExchangeRate.of(RateValue.of(34.004), RateValue.of(26.108))),
                        null, "AGENT_SMITH-" + i, valueDateWises, null, Boolean.TRUE));
            }
        }
        return data;
    }

    public static List<Timesheet> getTimesheets() {
        List<Timesheet> data = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
//            data.add(Timesheet.of("Euphoria", Boolean.TRUE, Arrays.stream(DayOfWeek.values())
//                    .collect(Collectors.toMap(day -> day,
//                            day -> Duration.ofHours(8),
//                    (e1, e2) -> e1, () -> new LinkedHashMap<DayOfWeek, Duration>(7)))));
            data.add(Timesheet.of("Euphoria", Boolean.TRUE));
        }

        return data;
    }
}
