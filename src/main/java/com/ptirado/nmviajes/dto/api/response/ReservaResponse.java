package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservaResponse {

    private Integer idReserva;
    private BigDecimal totalPagar;
    private String estadoReserva;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Datos del usuario
    private Integer idUsuario;
    private String nombreUsuario;
    private String emailUsuario;

    // Items de la reserva (paquetes)
    private List<ReservaItemResponse> items;
}
