package com.zcompany.example.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateTimeHelper {

    public static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    public static DateTimeFormatter DEFAULT_ZONED_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm:ssa (O)");

    public static ZoneId DISPLAY_ZONE_ID = ZoneId.of("UTC");

    public static String formatDate(final LocalDate date) {
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    public static String formatDate(final LocalDate date, final String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDate(final LocalDate date, final DateTimeFormatter formatter) {
        return date.format(formatter);
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DEFAULT_ZONED_DATE_TIME_FORMATTER);
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime, final String pattern) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatZonedDateTime(final ZonedDateTime zonedDateTime, final DateTimeFormatter formatter) {
        return zonedDateTime.format(formatter);
    }

    public static ZonedDateTime nowZonedDateTimeUTC() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public static ZonedDateTime nowZonedDateTime(final ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    public static ZonedDateTime convertZonedDateTime(final ZonedDateTime sourceZoneDatetime,
            final ZoneId targetZoneId) {
        return sourceZoneDatetime.getZone() == targetZoneId ? sourceZoneDatetime
                : sourceZoneDatetime.withZoneSameInstant(targetZoneId);
    }

    public static String convertAndFormatZonedDateTime(final ZonedDateTime sourceZoneDatetime,
            final ZoneId targetZoneId) {
        return sourceZoneDatetime.getZone() == targetZoneId ? formatZonedDateTime(sourceZoneDatetime)
                : formatZonedDateTime(sourceZoneDatetime.withZoneSameInstant(targetZoneId));
    }

    public static String convertAndFormatZonedDateTime(final ZonedDateTime sourceZoneDatetime) {
        return sourceZoneDatetime.getZone() == DISPLAY_ZONE_ID ? formatZonedDateTime(sourceZoneDatetime)
                : formatZonedDateTime(sourceZoneDatetime.withZoneSameInstant(DISPLAY_ZONE_ID));
    }
}
