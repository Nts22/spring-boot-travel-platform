package com.ptirado.nmviajes.viewmodel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaView {
    private Integer idReserva;
    private String totalPagarFormateado;
    private String estadoReserva;        // Texto formateado para mostrar
    private String estadoReservaCode;    // Codigo del enum (PENDIENTE, PAGADA, CANCELADA)
    private boolean finalizada;
    private boolean cancelada;
    private String fechaCreacionFormateada;

    // Datos del usuario
    private Integer idUsuario;
    private String nombreCompletoUsuario;
    private String emailUsuario;

    // Items de la reserva (paquetes)
    private List<ReservaItemView> items;
}
