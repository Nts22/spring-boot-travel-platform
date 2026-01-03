package com.ptirado.nmviajes.viewmodel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaItemView {
    private Integer idItem;
    private String fechaViajeInicioFormateada;
    private String subtotalFormateado;

    // Datos del paquete
    private Integer idPaquete;
    private String nombrePaquete;
    private String precioPaqueteFormateado;
    private String nombreDestino;

    // Servicios adicionales del item
    private List<ReservaItemServicioView> serviciosAdicionales;
}
