package com.ptirado.nmviajes.viewmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaqueteView {
    private Integer idPaquete;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String fechaInicioFormateada;
    private String fechaFinFormateada;
    private String stockDisponible;
    private String estado;
    private Integer idDestino;
    private String nombreDestino;
    private String fechaCreacionFormateada;
    private String fechaModificacionFormateada;
}
