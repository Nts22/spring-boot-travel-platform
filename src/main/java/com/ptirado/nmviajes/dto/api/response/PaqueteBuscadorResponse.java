package com.ptirado.nmviajes.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resultados del buscador de paquetes.
 * Todos los campos vienen formateados desde el controlador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaqueteBuscadorResponse {

    private Integer idPaquete;
    private String nombre;
    private String descripcion;
    private String precio;
    private String fechaInicio;
    private String fechaFin;
    private Integer stockDisponible;
    private String nombreDestino;
}
