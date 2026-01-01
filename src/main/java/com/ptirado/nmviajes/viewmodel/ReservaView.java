package com.ptirado.nmviajes.viewmodel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaView {
    private Integer idReserva;
    private String fechaViajeInicioFormateada;
    private String totalPagarFormateado;
    private String estadoReserva;
    private String estado;
    private String fechaCreacionFormateada;

    // Datos del usuario
    private Integer idUsuario;
    private String nombreCompletoUsuario;
    private String emailUsuario;

    // Datos del paquete
    private Integer idPaquete;
    private String nombrePaquete;
    private String precioPaqueteFormateado;
    private String nombreDestino;

    // Servicios adicionales
    private List<ReservaServicioView> serviciosAdicionales;
}
