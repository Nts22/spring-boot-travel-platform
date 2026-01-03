package com.ptirado.nmviajes.dto.api.response;

import java.math.BigDecimal;
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
public class CarritoResponse {

    private Integer idCarrito;
    private Integer idUsuario;
    private Integer cantidadItems;
    private BigDecimal totalCarrito;
    private List<CarritoItemResponse> items;
}
