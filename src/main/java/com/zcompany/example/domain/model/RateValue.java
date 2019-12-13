package com.zcompany.example.domain.model;

import java.math.BigDecimal;

public interface RateValue {

    public static final int DECIMAL_POINTS_8 = 8;

    public static final int DECIMAL_POINTS_3 = 3;

    public static final BigDecimal ZERO = BigDecimal.ZERO;

    public static final BigDecimal ONE = BigDecimal.ONE;

    public static BigDecimal preciseTo8Decimals(final BigDecimal bigDecimal) {
        return preciseToDecimals(bigDecimal, DECIMAL_POINTS_8);
    }

    public static BigDecimal preciseToDecimals(final BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal preciseTo3Decimals(final BigDecimal bigDecimal) {
        return preciseTo3Decimals(bigDecimal, DECIMAL_POINTS_3);
    }

    public static BigDecimal preciseTo3Decimals(final BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean isNegative(final BigDecimal value) {
        return value.compareTo(ZERO) < 0;
    }

    public static boolean isZero(final BigDecimal value) {
        return value.compareTo(ZERO) == 0;
    }

    public static boolean isOne(final BigDecimal value) {
        return value.compareTo(ONE) == 0;
    }

    public static BigDecimal of(final double value) {
        return new BigDecimal(value);
    }
}
