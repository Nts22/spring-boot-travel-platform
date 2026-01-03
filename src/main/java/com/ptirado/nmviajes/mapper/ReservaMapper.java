package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.response.ReservaItemResponse;
import com.ptirado.nmviajes.dto.api.response.ReservaItemServicioResponse;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.ReservaItem;
import com.ptirado.nmviajes.entity.ReservaItemServicio;
import com.ptirado.nmviajes.viewmodel.ReservaItemServicioView;
import com.ptirado.nmviajes.viewmodel.ReservaItemView;
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
                .totalPagar(reserva.getTotalPagar())
                .estadoReserva(reserva.getEstadoReserva())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                // Usuario
                .idUsuario(reserva.getUsuario() != null ? reserva.getUsuario().getIdUsuario() : null)
                .nombreUsuario(reserva.getUsuario() != null ?
                    reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido() : null)
                .emailUsuario(reserva.getUsuario() != null ? reserva.getUsuario().getEmail() : null)
                // Items
                .items(toItemResponseList(reserva.getItems()))
                .build();
    }

    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if (reservas == null) return List.of();
        return reservas.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    public ReservaItemResponse toItemResponse(ReservaItem item) {
        if (item == null) return null;

        return ReservaItemResponse.builder()
                .idItem(item.getIdItem())
                .fechaViajeInicio(item.getFechaViajeInicio())
                .subtotal(item.getSubtotal())
                // Paquete
                .idPaquete(item.getPaquete() != null ? item.getPaquete().getIdPaquete() : null)
                .nombrePaquete(item.getPaquete() != null ? item.getPaquete().getNombre() : null)
                .precioPaquete(item.getPaquete() != null ? item.getPaquete().getPrecio() : null)
                .nombreDestino(item.getPaquete() != null && item.getPaquete().getDestino() != null ?
                    item.getPaquete().getDestino().getNombre() : null)
                // Servicios
                .serviciosAdicionales(toItemServicioResponseList(item.getServicios()))
                .build();
    }

    public List<ReservaItemResponse> toItemResponseList(List<ReservaItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemResponse)
                .toList();
    }

    public ReservaItemServicioResponse toItemServicioResponse(ReservaItemServicio ris) {
        if (ris == null) return null;

        BigDecimal costoUnitario = ris.getServicioAdicional() != null ?
            ris.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = ris.getCantidad() != null ? ris.getCantidad() : 0;
        BigDecimal subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));

        return ReservaItemServicioResponse.builder()
                .idServicio(ris.getServicioAdicional() != null ?
                    ris.getServicioAdicional().getIdServicio() : null)
                .nombreServicio(ris.getServicioAdicional() != null ?
                    ris.getServicioAdicional().getNombre() : null)
                .costoUnitario(costoUnitario)
                .cantidad(cantidad)
                .subtotal(subtotal)
                .build();
    }

    public List<ReservaItemServicioResponse> toItemServicioResponseList(List<ReservaItemServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toItemServicioResponse)
                .toList();
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    public ReservaView toViewModelFromEntity(Reserva reserva) {
        if (reserva == null) return null;

        ReservaView view = new ReservaView();
        view.setIdReserva(reserva.getIdReserva());
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

        // Items
        view.setItems(toItemViewList(reserva.getItems()));

        return view;
    }

    public List<ReservaView> toViewList(List<Reserva> reservas) {
        if (reservas == null) return List.of();
        return reservas.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }

    public ReservaItemView toItemView(ReservaItem item) {
        if (item == null) return null;

        ReservaItemView view = new ReservaItemView();
        view.setIdItem(item.getIdItem());
        view.setFechaViajeInicioFormateada(formatearFecha(item.getFechaViajeInicio()));
        view.setSubtotalFormateado(formatearPrecio(item.getSubtotal()));

        // Paquete
        if (item.getPaquete() != null) {
            view.setIdPaquete(item.getPaquete().getIdPaquete());
            view.setNombrePaquete(item.getPaquete().getNombre());
            view.setPrecioPaqueteFormateado(formatearPrecio(item.getPaquete().getPrecio()));
            if (item.getPaquete().getDestino() != null) {
                view.setNombreDestino(item.getPaquete().getDestino().getNombre());
            }
        }

        // Servicios
        view.setServiciosAdicionales(toItemServicioViewList(item.getServicios()));

        return view;
    }

    public List<ReservaItemView> toItemViewList(List<ReservaItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemView)
                .toList();
    }

    public ReservaItemServicioView toItemServicioView(ReservaItemServicio ris) {
        if (ris == null) return null;

        BigDecimal costoUnitario = ris.getServicioAdicional() != null ?
            ris.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = ris.getCantidad() != null ? ris.getCantidad() : 0;
        BigDecimal subtotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));

        ReservaItemServicioView view = new ReservaItemServicioView();
        view.setIdServicio(ris.getServicioAdicional() != null ?
            ris.getServicioAdicional().getIdServicio() : null);
        view.setNombreServicio(ris.getServicioAdicional() != null ?
            ris.getServicioAdicional().getNombre() : null);
        view.setCostoUnitarioFormateado(formatearPrecio(costoUnitario));
        view.setCantidad(cantidad);
        view.setSubtotalFormateado(formatearPrecio(subtotal));

        return view;
    }

    public List<ReservaItemServicioView> toItemServicioViewList(List<ReservaItemServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toItemServicioView)
                .toList();
    }
}
