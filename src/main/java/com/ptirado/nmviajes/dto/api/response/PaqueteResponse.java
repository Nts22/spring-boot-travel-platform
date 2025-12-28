package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class PaqueteResponse {

    private Integer idPaquete;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer stockDisponible;
    private String estado;
    private Integer idDestino;
    private String nombreDestino;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}