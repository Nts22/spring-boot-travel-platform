package com.ptirado.nmviajes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogStatsService {

    private static final Logger log = LoggerFactory.getLogger(LogStatsService.class);
    private static final String LOG_FILE_PATH = "logs/nmviajes.log";
    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*?(ERROR|WARN|INFO|DEBUG|TRACE)");

    /**
     * Obtiene estadísticas de errores agrupados por día
     */
    public Map<String, Map<String, Integer>> getErrorStatsByDay(int days) {
        Map<String, Map<String, Integer>> stats = new LinkedHashMap<>();

        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(logPath)) {
                return stats;
            }

            List<String> lines = Files.readAllLines(logPath);
            LocalDate cutoffDate = LocalDate.now().minusDays(days);

            for (String line : lines) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String dateTimeStr = matcher.group(1);
                    String level = matcher.group(2);

                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate logDate = dateTime.toLocalDate();

                        // Solo contar logs de los últimos N días
                        if (!logDate.isBefore(cutoffDate)) {
                            String dateKey = logDate.toString();
                            stats.putIfAbsent(dateKey, new HashMap<>());
                            Map<String, Integer> levelCounts = stats.get(dateKey);
                            levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
                        }
                    } catch (Exception e) {
                        // Ignorar líneas con formato de fecha inválido
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error al leer estadísticas de logs", e);
        }

        return stats;
    }

    /**
     * Obtiene estadísticas de errores agrupados por hora (últimas 24 horas)
     */
    public Map<String, Map<String, Integer>> getErrorStatsByHour() {
        Map<String, Map<String, Integer>> stats = new LinkedHashMap<>();

        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(logPath)) {
                return stats;
            }

            List<String> lines = Files.readAllLines(logPath);
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

            for (String line : lines) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String dateTimeStr = matcher.group(1);
                    String level = matcher.group(2);

                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        // Solo contar logs de las últimas 24 horas
                        if (dateTime.isAfter(cutoffTime)) {
                            String hourKey = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
                            stats.putIfAbsent(hourKey, new HashMap<>());
                            Map<String, Integer> levelCounts = stats.get(hourKey);
                            levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
                        }
                    } catch (Exception e) {
                        // Ignorar líneas con formato de fecha inválido
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error al leer estadísticas de logs por hora", e);
        }

        return stats;
    }

    /**
     * Obtiene resumen de logs por nivel
     */
    public Map<String, Integer> getLogLevelSummary() {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("ERROR", 0);
        summary.put("WARN", 0);
        summary.put("INFO", 0);
        summary.put("DEBUG", 0);
        summary.put("TRACE", 0);

        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(logPath)) {
                return summary;
            }

            List<String> lines = Files.readAllLines(logPath);

            for (String line : lines) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    String level = matcher.group(2);
                    summary.put(level, summary.getOrDefault(level, 0) + 1);
                }
            }

        } catch (IOException e) {
            log.error("Error al obtener resumen de niveles de log", e);
        }

        return summary;
    }

    /**
     * Prepara datos para Chart.js (formato JSON)
     */
    public Map<String, Object> getChartDataByDay(int days) {
        Map<String, Map<String, Integer>> stats = getErrorStatsByDay(days);

        Map<String, Object> chartData = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> errorData = new ArrayList<>();
        List<Integer> warnData = new ArrayList<>();
        List<Integer> infoData = new ArrayList<>();

        // Generar labels para todos los días (incluso si no tienen logs)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateKey = date.toString();
            labels.add(dateKey);

            Map<String, Integer> dayCounts = stats.getOrDefault(dateKey, new HashMap<>());
            errorData.add(dayCounts.getOrDefault("ERROR", 0));
            warnData.add(dayCounts.getOrDefault("WARN", 0));
            infoData.add(dayCounts.getOrDefault("INFO", 0));
        }

        chartData.put("labels", labels);
        chartData.put("errorData", errorData);
        chartData.put("warnData", warnData);
        chartData.put("infoData", infoData);

        return chartData;
    }

    /**
     * Prepara datos para Chart.js por hora
     */
    public Map<String, Object> getChartDataByHour() {
        Map<String, Map<String, Integer>> stats = getErrorStatsByHour();

        Map<String, Object> chartData = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> errorData = new ArrayList<>();
        List<Integer> warnData = new ArrayList<>();
        List<Integer> infoData = new ArrayList<>();

        // Generar labels para las últimas 24 horas
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(23);

        for (LocalDateTime time = startTime; !time.isAfter(endTime); time = time.plusHours(1)) {
            String hourKey = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
            String labelKey = time.format(DateTimeFormatter.ofPattern("HH:00"));
            labels.add(labelKey);

            Map<String, Integer> hourCounts = stats.getOrDefault(hourKey, new HashMap<>());
            errorData.add(hourCounts.getOrDefault("ERROR", 0));
            warnData.add(hourCounts.getOrDefault("WARN", 0));
            infoData.add(hourCounts.getOrDefault("INFO", 0));
        }

        chartData.put("labels", labels);
        chartData.put("errorData", errorData);
        chartData.put("warnData", warnData);
        chartData.put("infoData", infoData);

        return chartData;
    }
}
