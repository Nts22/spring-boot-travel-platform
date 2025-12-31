package com.ptirado.nmviajes.dto.form;

import static com.ptirado.nmviajes.constants.ValidationConstants.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaqueteForm {

    private Integer idPaquete;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = PAQUETE_NOMBRE_MIN, max = PAQUETE_NOMBRE_MAX,
            message = "El nombre debe tener entre " + PAQUETE_NOMBRE_MIN + " y " + PAQUETE_NOMBRE_MAX + " caracteres")
    private String nombre;

    @Size(max = PAQUETE_DESCRIPCION_MAX,
            message = "La descripcion debe tener maximo " + PAQUETE_DESCRIPCION_MAX + " caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = PRECIO_MIN, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o una fecha futura")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser una fecha futura")
    private LocalDate fechaFin;

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;

    @Size(max = ESTADO_MAX, message = "El estado debe tener maximo " + ESTADO_MAX + " caracteres")
    private String estado;

    @NotNull(message = "El destino es obligatorio")
    private Integer idDestino;
}