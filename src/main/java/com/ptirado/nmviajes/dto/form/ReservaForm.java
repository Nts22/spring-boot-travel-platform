package com.ptirado.nmviajes.dto.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaForm {

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "El paquete es obligatorio")
    private Integer idPaquete;

    @NotNull(message = "La fecha de inicio del viaje es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o una fecha futura")
    private LocalDate fechaViajeInicio;

    @Builder.Default
    private List<ServicioSeleccionado> serviciosSeleccionados = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicioSeleccionado {
        private Integer idServicio;
        private Integer cantidad;
    }
}
