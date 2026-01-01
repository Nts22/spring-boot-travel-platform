package com.ptirado.nmviajes.dto.api.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
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
public class ReservaRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "El paquete es obligatorio")
    private Integer idPaquete;

    @NotNull(message = "La fecha de inicio del viaje es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o una fecha futura")
    private LocalDate fechaViajeInicio;

    @Valid
    @Size(max = 10, message = "Maximo 10 servicios adicionales por reserva")
    private List<ServicioAdicionalItemRequest> serviciosAdicionales;
}
