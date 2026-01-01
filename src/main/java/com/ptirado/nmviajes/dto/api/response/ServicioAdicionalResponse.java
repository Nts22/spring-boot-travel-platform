package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;

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
public class ServicioAdicionalResponse {

    private Integer idServicio;
    private String nombre;
    private BigDecimal costo;
    private String estado;
}
