package com.ptirado.nmviajes.constants;

/**
 * Constantes de rutas para la configuracion de seguridad.
 * Centraliza todas las rutas publicas y protegidas de la aplicacion.
 */
public final class SecurityPaths {

    private SecurityPaths() {}

    // ═══════════════════════════════════════════════════════════════════════════
    // RECURSOS ESTATICOS
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] STATIC_RESOURCES = {
        "/css/**",
        "/js/**",
        "/img/**",
        "/images/**",
        "/webjars/**",
        "/favicon.ico"
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // PAGINAS WEB PUBLICAS
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] PUBLIC_PAGES = {
        "/",
        "/index",
        "/home",
        "/login",
        "/registro",
        "/error",
        "/acceso-denegado"
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // VISTAS WEB PUBLICAS (Catalogo)
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] PUBLIC_CATALOG_VIEWS = {
        "/paquetes",
        "/paquetes/**",
        "/destinos",
        "/destinos/**"
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // API PUBLICA
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] PUBLIC_API = {
        "/api/v1/destinos/**",
        "/api/v1/paquetes/**",
        "/api/v1/servicios/**",
        "/api/v1/contacto",
        "/api/v1/auth/**"
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // RUTAS DE ADMINISTRADOR
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] ADMIN_PAGES = {
        "/admin/**"
    };

    public static final String[] ADMIN_API = {
        "/api/v1/admin/**"
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // RUTAS EXCLUIDAS DE CSRF
    // ═══════════════════════════════════════════════════════════════════════════
    public static final String[] CSRF_IGNORED = {
        "/api/**"
    };
}
