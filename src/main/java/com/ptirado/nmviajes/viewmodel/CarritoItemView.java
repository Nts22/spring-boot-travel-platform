package com.ptirado.nmviajes.viewmodel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarritoItemView {
    private Integer idItem;
    private String fechaViajeInicioFormateada;
    private String fechaAgregadoFormateada;

    // Datos del paquete
    private Integer idPaquete;
    private String nombrePaquete;
    private String nombreDestino;
    private String precioPaqueteFormateado;
    private String fechaInicioPaquete;
    private String fechaFinPaquete;
    private Integer stockDisponible;

    // Servicios adicionales
    private List<CarritoItemServicioView> serviciosAdicionales;

    // Subtotal formateado
    private String subtotalFormateado;
}
