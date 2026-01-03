package com.ptirado.nmviajes.viewmodel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarritoView {
    private Integer idCarrito;
    private Integer idUsuario;
    private Integer cantidadItems;
    private String totalCarritoFormateado;
    private List<CarritoItemView> items;
}
