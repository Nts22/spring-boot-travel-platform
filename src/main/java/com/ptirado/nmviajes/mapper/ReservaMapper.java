package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.api.response.ReservaServicioResponse;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.ReservaServicio;
import com.ptirado.nmviajes.viewmodel.ReservaServicioView;
import com.ptirado.nmviajes.viewmodel.ReservaView;

@Component
public class ReservaMapper {

    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance(LOCALE_PE);

    static {
        PRICE_FORMATTER.setMinimumFractionDigits(2);
        PRICE_FORMATTER.setMaximumFractionDigits(2);
    }

    // ===========================================================
    //               METODOS DE FORMATEO
    // ===========================================================

    private String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }

    private String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora != null ? fechaHora.format(DATETIME_FORMATTER) : "-";
    }

    private String formatearPrecio(BigDecimal precio) {
        return precio != null ? "S/ " + PRICE_FORMATTER.format(precio) : "S/ 0.00";
    }

    // ===========================================================
    //               MAPEOS PARA API (JSON)
    // ===========================================================

    public ReservaResponse toResponseFromEntity(Reserva reserva) {
        if (reserva == null) return null;

        return ReservaResponse.builder()
                .idReserva(reserva.getIdReserva())
                .fechaViajeInicio(reserva.getFechaViajeInicio())
                .totalPagar(reserva.getTotalPagar())
                .estadoReserva(reserva.getEstadoReserva())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                // Usuario
                .idUsuario(reserva.getUsuario() != null ? reserva.getUsuario().getIdUsuario() : null)
                .nombreUsuario(reserva.getUsuario() != null ?
                    reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido() : null)
                .emailUsuario(reserva.getUsuario() != null ? reserva.getUsuario().getEmail() : null)
                // Paquete
                .idPaquete(reserva.getPaquete() != null ? reserva.getPaquete().getIdPaquete() : null)
                .nombrePaquete(reserva.getPaquete() != null ? reserva.getPaquete().getNombre() : null)
                .precioPaquete(reserva.getPaquete() != null ? reserva.getPaquete().getPrecio() : null)
                // Servicios
                .serviciosAdicionales(toServicioResponseList(reserva.getReservasServicios()))
                .build();
    }

    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if (reservas == null) return List.of();
        return reservas.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    public ReservaServicioResponse toServicioResponse(ReservaServicio rs) {
        if (rs == null) return null;

        BigDecimal costoUnitario = rs.getServicioAdicional() != null ?
            rs.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = rs.getCantidad() != null ? rs.getCantidad() : 0;
        BigDecimal subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));

        return ReservaServicioResponse.builder()
                .idServicio(rs.getServicioAdicional() != null ?
                    rs.getServicioAdicional().getIdServicio() : null)
                .nombreServicio(rs.getServicioAdicional() != null ?
                    rs.getServicioAdicional().getNombre() : null)
                .costoUnitario(costoUnitario)
                .cantidad(cantidad)
                .subtotal(subtotal)
                .build();
    }

    public List<ReservaServicioResponse> toServicioResponseList(List<ReservaServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toServicioResponse)
                .toList();
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    public ReservaView toViewModelFromEntity(Reserva reserva) {
        if (reserva == null) return null;

        ReservaView view = new ReservaView();
        view.setIdReserva(reserva.getIdReserva());
        view.setFechaViajeInicioFormateada(formatearFecha(reserva.getFechaViajeInicio()));
        view.setTotalPagarFormateado(formatearPrecio(reserva.getTotalPagar()));
        view.setEstadoReserva(reserva.getEstadoReserva());
        view.setEstado(reserva.getEstado());
        view.setFechaCreacionFormateada(formatearFechaHora(reserva.getFechaCreacion()));

        // Usuario
        if (reserva.getUsuario() != null) {
            view.setIdUsuario(reserva.getUsuario().getIdUsuario());
            view.setNombreCompletoUsuario(
                reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido());
            view.setEmailUsuario(reserva.getUsuario().getEmail());
        }

        // Paquete
        if (reserva.getPaquete() != null) {
            view.setIdPaquete(reserva.getPaquete().getIdPaquete());
            view.setNombrePaquete(reserva.getPaquete().getNombre());
            view.setPrecioPaqueteFormateado(formatearPrecio(reserva.getPaquete().getPrecio()));
            if (reserva.getPaquete().getDestino() != null) {
                view.setNombreDestino(reserva.getPaquete().getDestino().getNombre());
            }
        }

        // Servicios
        view.setServiciosAdicionales(toServicioViewList(reserva.getReservasServicios()));

        return view;
    }

    public List<ReservaView> toViewList(List<Reserva> reservas) {
        if (reservas == null) return List.of();
        return reservas.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }

    public ReservaServicioView toServicioView(ReservaServicio rs) {
        if (rs == null) return null;

        BigDecimal costoUnitario = rs.getServicioAdicional() != null ?
            rs.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = rs.getCantidad() != null ? rs.getCantidad() : 0;
        BigDecimal subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));

        ReservaServicioView view = new ReservaServicioView();
        view.setIdServicio(rs.getServicioAdicional() != null ?
            rs.getServicioAdicional().getIdServicio() : null);
        view.setNombreServicio(rs.getServicioAdicional() != null ?
            rs.getServicioAdicional().getNombre() : null);
        view.setCostoUnitarioFormateado(formatearPrecio(costoUnitario));
        view.setCantidad(cantidad);
        view.setSubtotalFormateado(formatearPrecio(subtotal));

        return view;
    }

    public List<ReservaServicioView> toServicioViewList(List<ReservaServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toServicioView)
                .toList();
    }
}
