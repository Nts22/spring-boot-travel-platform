package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ReservaItemResponse {

    private Integer idItem;
    private LocalDate fechaViajeInicio;
    private BigDecimal subtotal;

    // Datos del paquete
    private Integer idPaquete;
    private String nombrePaquete;
    private BigDecimal precioPaquete;
    private String nombreDestino;

    // Servicios adicionales del item
    private List<ReservaItemServicioResponse> serviciosAdicionales;
}
