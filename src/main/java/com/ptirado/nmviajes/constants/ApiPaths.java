package com.ptirado.nmviajes.constants;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String API_BASE = "/api/v1";

    // DESTINO
    public static final String DESTINOS = API_BASE + "/destinos";
    public static final String DESTINOS_ID = "/{id}";

    // PAQUETE
    public static final String PAQUETES = API_BASE + "/paquetes";
    public static final String PAQUETES_ID =  "/{id}";

    // USUARIO
    public static final String USUARIOS = API_BASE + "/usuarios";
    public static final String USUARIOS_ID =  "/{id}";

    // RESERVAS
    public static final String RESERVAS = API_BASE + "/reservas";
    public static final String RESERVAS_ID = "/{id}";
}
