package com.ptirado.nmviajes.viewmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaItemServicioView {
    private Integer idServicio;
    private String nombreServicio;
    private String costoUnitarioFormateado;
    private Integer cantidad;
    private String subtotalFormateado;
}
