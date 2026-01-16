package com.ptirado.nmviajes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    private static final Logger log = LoggerFactory.getLogger(LogService.class);
    private static final String LOG_FILE_PATH = "logs/nmviajes.log";

    /**
     * Obtiene las últimas N líneas del archivo de log
     */
    public List<String> getLatestLogs(int maxLines) {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);

            if (!Files.exists(logPath)) {
                log.warn("Archivo de log no encontrado: {}", LOG_FILE_PATH);
                return Collections.singletonList("⚠️ Archivo de log no encontrado. El archivo se creará cuando la aplicación genere logs.");
            }

            List<String> allLines = Files.readAllLines(logPath);

            if (allLines.isEmpty()) {
                return Collections.singletonList("📄 El archivo de log está vacío.");
            }

            // Obtener las últimas N líneas
            int startIndex = Math.max(0, allLines.size() - maxLines);
            List<String> latestLines = new ArrayList<>(allLines.subList(startIndex, allLines.size()));

            // Invertir para mostrar las más recientes primero
            Collections.reverse(latestLines);

            return latestLines;

        } catch (IOException e) {
            log.error("Error al leer el archivo de log", e);
            return Collections.singletonList("❌ Error al leer el archivo de log: " + e.getMessage());
        }
    }

    /**
     * Filtra logs por nivel (ERROR, WARN, INFO, DEBUG)
     */
    public List<String> filterLogsByLevel(List<String> logs, String level) {
        if (level == null || level.isEmpty() || level.equals("ALL")) {
            return logs;
        }

        return logs.stream()
                .filter(line -> line.contains(level))
                .collect(Collectors.toList());
    }

    /**
     * Busca logs que contengan un texto específico
     */
    public List<String> searchLogs(List<String> logs, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return logs;
        }

        String searchLower = searchText.toLowerCase();
        return logs.stream()
                .filter(line -> line.toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la ruta completa del archivo de log
     */
    public Path getLogFilePath() {
        return Paths.get(LOG_FILE_PATH).toAbsolutePath();
    }

    /**
     * Verifica si el archivo de log existe
     */
    public boolean logFileExists() {
        return Files.exists(Paths.get(LOG_FILE_PATH));
    }

    /**
     * Obtiene el tamaño del archivo de log en bytes
     */
    public long getLogFileSize() {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            if (Files.exists(logPath)) {
                return Files.size(logPath);
            }
            return 0;
        } catch (IOException e) {
            log.error("Error al obtener el tamaño del archivo de log", e);
            return 0;
        }
    }

    /**
     * Formatea el tamaño del archivo en formato legible (KB, MB)
     */
    public String getFormattedLogFileSize() {
        long bytes = getLogFileSize();

        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}
