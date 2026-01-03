package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class CarritoItemResponse {

    private Integer idItem;
    private LocalDate fechaViajeInicio;
    private LocalDateTime fechaAgregado;

    // Datos del paquete
    private Integer idPaquete;
    private String nombrePaquete;
    private String nombreDestino;
    private BigDecimal precioPaquete;
    private String fechaInicioPaquete;
    private String fechaFinPaquete;

    // Servicios adicionales
    private List<CarritoItemServicioResponse> serviciosAdicionales;

    // Subtotal (paquete + servicios)
    private BigDecimal subtotal;
}
