package com.ptirado.nmviajes.constants;

/**
 * Constantes de validacion centralizadas.
 * Usar estas constantes en los DTOs para mantener consistencia.
 */
public final class ValidationConstants {

    private ValidationConstants() {}

    // ================================
    //   Contacto
    // ================================
    public static final int CONTACTO_NOMBRE_MIN = 2;
    public static final int CONTACTO_NOMBRE_MAX = 100;
    public static final int CONTACTO_EMAIL_MAX = 150;
    public static final int CONTACTO_TELEFONO_MAX = 20;
    public static final int CONTACTO_MENSAJE_MIN = 10;
    public static final int CONTACTO_MENSAJE_MAX = 2000;

    // Patron para telefono: permite +, numeros, espacios y guiones
    public static final String TELEFONO_PATTERN = "^[+]?[0-9\\s\\-]{6,20}$";

    // ================================
    //   Paquete
    // ================================
    public static final int PAQUETE_NOMBRE_MIN = 3;
    public static final int PAQUETE_NOMBRE_MAX = 100;
    public static final int PAQUETE_DESCRIPCION_MAX = 1000;
    public static final int ESTADO_MAX = 3;

    // ================================
    //   Destino
    // ================================
    public static final int DESTINO_NOMBRE_MIN = 3;
    public static final int DESTINO_NOMBRE_MAX = 100;
    public static final int DESTINO_PAIS_MAX = 100;
    public static final int DESTINO_DESCRIPCION_MAX = 1000;

    // ================================
    //   Generales
    // ================================
    public static final String PRECIO_MIN = "0.01";
}
