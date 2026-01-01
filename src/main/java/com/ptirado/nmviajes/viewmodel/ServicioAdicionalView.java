package com.ptirado.nmviajes.viewmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicioAdicionalView {
    private Integer idServicio;
    private String nombre;
    private String costoFormateado;
    private String estado;
}
