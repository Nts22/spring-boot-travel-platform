package com.ptirado.nmviajes.config;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * Configuracion centralizada para formateo de datos en la aplicacion.
 *
 * <p>Esta clase centraliza todos los formatos de fecha, hora y moneda
 * utilizados en la aplicacion, facilitando su mantenimiento y consistencia.</p>
 *
 * <h3>Uso en otros componentes:</h3>
 * <pre>
 * &#64;Autowired
 * private FormatConfig formatConfig;
 *
 * String precio = formatConfig.formatearPrecio(new BigDecimal("1500.50"));
 * // Resultado: "S/ 1,500.50"
 * </pre>
 *
 * <h3>Formatos disponibles:</h3>
 * <ul>
 *   <li><b>Fecha:</b> dd/MM/yyyy (ej: 15/01/2026)</li>
 *   <li><b>Fecha y hora:</b> dd/MM/yyyy HH:mm (ej: 15/01/2026 14:30)</li>
 *   <li><b>Precio:</b> S/ #,###.## (ej: S/ 1,500.00)</li>
 * </ul>
 *
 * @author Sistema NMViajes
 */
@Component
public class FormatConfig {

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                           CONFIGURACION REGIONAL                           ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Locale para Peru (es-PE).
     * Usado para formateo de numeros y moneda.
     */
    public static final Locale LOCALE_PE = new Locale("es", "PE");

    /**
     * Simbolo de moneda (Soles peruanos).
     */
    public static final String SIMBOLO_MONEDA = "S/";

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                          PATRONES DE FORMATO                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Patron de formato para fechas: dd/MM/yyyy
     * Ejemplo: 15/01/2026
     */
    public static final String PATRON_FECHA = "dd/MM/yyyy";

    /**
     * Patron de formato para fecha y hora: dd/MM/yyyy HH:mm
     * Ejemplo: 15/01/2026 14:30
     */
    public static final String PATRON_FECHA_HORA = "dd/MM/yyyy HH:mm";

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                            FORMATEADORES                                    ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(PATRON_FECHA);

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(PATRON_FECHA_HORA);

    private static final NumberFormat PRICE_FORMATTER;

    static {
        PRICE_FORMATTER = NumberFormat.getNumberInstance(LOCALE_PE);
        PRICE_FORMATTER.setMinimumFractionDigits(2);
        PRICE_FORMATTER.setMaximumFractionDigits(2);
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         METODOS DE FORMATEO                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Formatea una fecha al formato dd/MM/yyyy.
     *
     * @param fecha La fecha a formatear (puede ser null)
     * @return La fecha formateada o cadena vacia si es null
     *
     * @example
     * formatearFecha(LocalDate.of(2026, 1, 15)) // "15/01/2026"
     * formatearFecha(null) // ""
     */
    public String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }

    /**
     * Formatea una fecha con hora al formato dd/MM/yyyy HH:mm.
     *
     * @param fechaHora La fecha y hora a formatear (puede ser null)
     * @return La fecha formateada o guion si es null
     *
     * @example
     * formatearFechaHora(LocalDateTime.of(2026, 1, 15, 14, 30)) // "15/01/2026 14:30"
     * formatearFechaHora(null) // "-"
     */
    public String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora != null ? fechaHora.format(DATETIME_FORMATTER) : "-";
    }

    /**
     * Formatea un precio con el simbolo de moneda (Soles).
     *
     * @param precio El precio a formatear (puede ser null)
     * @return El precio formateado con simbolo de moneda
     *
     * @example
     * formatearPrecio(new BigDecimal("1500.50")) // "S/ 1,500.50"
     * formatearPrecio(null) // "S/ 0.00"
     */
    public String formatearPrecio(BigDecimal precio) {
        if (precio == null) {
            return SIMBOLO_MONEDA + " 0.00";
        }
        return SIMBOLO_MONEDA + " " + PRICE_FORMATTER.format(precio);
    }

    /**
     * Formatea un precio sin el simbolo de moneda.
     *
     * @param precio El precio a formatear (puede ser null)
     * @return El precio formateado sin simbolo
     *
     * @example
     * formatearPrecioSinSimbolo(new BigDecimal("1500.50")) // "1,500.50"
     */
    public String formatearPrecioSinSimbolo(BigDecimal precio) {
        if (precio == null) {
            return "0.00";
        }
        return PRICE_FORMATTER.format(precio);
    }
}
