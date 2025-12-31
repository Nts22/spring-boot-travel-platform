package com.ptirado.nmviajes.dto.form;

import static com.ptirado.nmviajes.constants.ValidationConstants.*;

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
    @Size(min = DESTINO_NOMBRE_MIN, max = DESTINO_NOMBRE_MAX,
            message = "El nombre debe tener entre " + DESTINO_NOMBRE_MIN + " y " + DESTINO_NOMBRE_MAX + " caracteres")
    private String nombre;

    @NotBlank(message = "El pais es obligatorio")
    @Size(max = DESTINO_PAIS_MAX,
            message = "El pais debe tener maximo " + DESTINO_PAIS_MAX + " caracteres")
    private String pais;

    @Size(max = DESTINO_DESCRIPCION_MAX,
            message = "La descripcion debe tener maximo " + DESTINO_DESCRIPCION_MAX + " caracteres")
    private String descripcion;

    @Size(max = ESTADO_MAX, message = "El estado debe tener maximo " + ESTADO_MAX + " caracteres")
    private String estado;
}
