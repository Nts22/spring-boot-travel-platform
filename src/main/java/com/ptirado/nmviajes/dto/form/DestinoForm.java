package com.ptirado.nmviajes.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinoForm {

    private Integer idDestino;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país debe tener máximo 100 caracteres")
    private String pais;

    @Size(max = 1000, message = "La descripción debe tener máximo 1000 caracteres")
    private String descripcion;

    @Size(max = 50, message = "El estado debe tener máximo 50 caracteres")
    private String estado;
    
    // NO incluimos fechas (se generan automáticamente en el backend)
}
