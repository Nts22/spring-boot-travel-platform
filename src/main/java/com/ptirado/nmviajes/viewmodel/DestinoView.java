package com.ptirado.nmviajes.viewmodel;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DestinoView {

    private Integer idDestino;

    private String nombre;

    private String pais;

    private String descripcion;

    private String estado;

    private String fechaCreacionFormateada;

    private String fechaModificacionFormateada;
}
