package com.ptirado.nmviajes.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtils {

    private DateUtils() {}

    // ==========================
    // FORMATOS
    // ==========================
    public static final DateTimeFormatter DEFAULT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormatter ISO_FORMAT =
            DateTimeFormatter.ISO_DATE_TIME;

    public static final DateTimeFormatter DATE_ONLY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter TIME_ONLY =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter TIMESTAMP_FILE =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // ==========================
    // FORMATEO
    // ==========================
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMAT) : null;
    }

    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_ONLY) : null;
    }

    public static String format(Instant instant) {
        return instant != null ?
                DEFAULT_FORMAT.format(instant.atZone(ZoneId.systemDefault()))
                : null;
    }

    // ==========================
    // PARSE
    // ==========================
    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, DEFAULT_FORMAT);
    }

    public static LocalDateTime parse(String dateStr, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static LocalDateTime parseDateTime(String text, String pattern) {
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }

    // ==========================
    // AHORA
    // ==========================
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ==========================
    // CÁLCULOS
    // ==========================
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static LocalDateTime addDays(LocalDateTime date, int days) {
        return date.plusDays(days);
    }

    public static LocalDateTime addHours(LocalDateTime date, int hours) {
        return date.plusHours(hours);
    }

    public static boolean isBefore(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b);
    }

    public static boolean isAfter(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b);
    }
    public static String format(LocalDateTime date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

}
