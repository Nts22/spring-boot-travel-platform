package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.response.CarritoItemResponse;
import com.ptirado.nmviajes.dto.api.response.CarritoItemServicioResponse;
import com.ptirado.nmviajes.dto.api.response.CarritoResponse;
import com.ptirado.nmviajes.entity.Carrito;
import com.ptirado.nmviajes.entity.CarritoItem;
import com.ptirado.nmviajes.entity.CarritoItemServicio;
import com.ptirado.nmviajes.viewmodel.CarritoItemServicioView;
import com.ptirado.nmviajes.viewmodel.CarritoItemView;
import com.ptirado.nmviajes.viewmodel.CarritoView;

@Component
public class CarritoMapper {

    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance(LOCALE_PE);

    static {
        PRICE_FORMATTER.setMinimumFractionDigits(2);
        PRICE_FORMATTER.setMaximumFractionDigits(2);
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }

    private String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora != null ? fechaHora.format(DATETIME_FORMATTER) : "-";
    }

    private String formatearPrecio(BigDecimal precio) {
        return precio != null ? "S/ " + PRICE_FORMATTER.format(precio) : "S/ 0.00";
    }

    private BigDecimal calcularSubtotalItem(CarritoItem item) {
        BigDecimal subtotal = item.getPaquete() != null ? item.getPaquete().getPrecio() : BigDecimal.ZERO;

        if (item.getServicios() != null) {
            for (CarritoItemServicio servicio : item.getServicios()) {
                BigDecimal costo = servicio.getServicioAdicional() != null ?
                    servicio.getServicioAdicional().getCosto() : BigDecimal.ZERO;
                Integer cantidad = servicio.getCantidad() != null ? servicio.getCantidad() : 0;
                subtotal = subtotal.add(costo.multiply(BigDecimal.valueOf(cantidad)));
            }
        }

        return subtotal;
    }

    // ===========================================================
    //               MAPEOS PARA API (JSON)
    // ===========================================================

    public CarritoResponse toResponse(Carrito carrito) {
        if (carrito == null) return null;

        List<CarritoItemResponse> items = toItemResponseList(carrito.getItems());
        BigDecimal total = items.stream()
            .map(CarritoItemResponse::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoResponse.builder()
                .idCarrito(carrito.getIdCarrito())
                .idUsuario(carrito.getUsuario() != null ? carrito.getUsuario().getIdUsuario() : null)
                .cantidadItems(carrito.getItems() != null ? carrito.getItems().size() : 0)
                .totalCarrito(total)
                .items(items)
                .build();
    }

    public CarritoItemResponse toItemResponse(CarritoItem item) {
        if (item == null) return null;

        BigDecimal subtotal = calcularSubtotalItem(item);

        return CarritoItemResponse.builder()
                .idItem(item.getIdItem())
                .fechaViajeInicio(item.getFechaViajeInicio())
                .fechaAgregado(item.getFechaAgregado())
                .idPaquete(item.getPaquete() != null ? item.getPaquete().getIdPaquete() : null)
                .nombrePaquete(item.getPaquete() != null ? item.getPaquete().getNombre() : null)
                .nombreDestino(item.getPaquete() != null && item.getPaquete().getDestino() != null ?
                    item.getPaquete().getDestino().getNombre() : null)
                .precioPaquete(item.getPaquete() != null ? item.getPaquete().getPrecio() : null)
                .fechaInicioPaquete(item.getPaquete() != null ?
                    formatearFecha(item.getPaquete().getFechaInicio()) : null)
                .fechaFinPaquete(item.getPaquete() != null ?
                    formatearFecha(item.getPaquete().getFechaFin()) : null)
                .serviciosAdicionales(toServicioResponseList(item.getServicios()))
                .subtotal(subtotal)
                .build();
    }

    public List<CarritoItemResponse> toItemResponseList(List<CarritoItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemResponse)
                .toList();
    }

    public CarritoItemServicioResponse toServicioResponse(CarritoItemServicio servicio) {
        if (servicio == null) return null;

        BigDecimal costo = servicio.getServicioAdicional() != null ?
            servicio.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = servicio.getCantidad() != null ? servicio.getCantidad() : 0;
        BigDecimal subtotal = costo.multiply(BigDecimal.valueOf(cantidad));

        return CarritoItemServicioResponse.builder()
                .idServicio(servicio.getServicioAdicional() != null ?
                    servicio.getServicioAdicional().getIdServicio() : null)
                .nombreServicio(servicio.getServicioAdicional() != null ?
                    servicio.getServicioAdicional().getNombre() : null)
                .costoUnitario(costo)
                .cantidad(cantidad)
                .subtotal(subtotal)
                .build();
    }

    public List<CarritoItemServicioResponse> toServicioResponseList(List<CarritoItemServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toServicioResponse)
                .toList();
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    public CarritoView toView(Carrito carrito) {
        if (carrito == null) return null;

        List<CarritoItemView> items = toItemViewList(carrito.getItems());
        BigDecimal total = carrito.getItems() != null ?
            carrito.getItems().stream()
                .map(this::calcularSubtotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        CarritoView view = new CarritoView();
        view.setIdCarrito(carrito.getIdCarrito());
        view.setIdUsuario(carrito.getUsuario() != null ? carrito.getUsuario().getIdUsuario() : null);
        view.setCantidadItems(carrito.getItems() != null ? carrito.getItems().size() : 0);
        view.setTotalCarritoFormateado(formatearPrecio(total));
        view.setItems(items);

        return view;
    }

    public CarritoItemView toItemView(CarritoItem item) {
        if (item == null) return null;

        BigDecimal subtotal = calcularSubtotalItem(item);

        CarritoItemView view = new CarritoItemView();
        view.setIdItem(item.getIdItem());
        view.setFechaViajeInicioFormateada(formatearFecha(item.getFechaViajeInicio()));
        view.setFechaAgregadoFormateada(formatearFechaHora(item.getFechaAgregado()));

        if (item.getPaquete() != null) {
            view.setIdPaquete(item.getPaquete().getIdPaquete());
            view.setNombrePaquete(item.getPaquete().getNombre());
            view.setPrecioPaqueteFormateado(formatearPrecio(item.getPaquete().getPrecio()));
            view.setFechaInicioPaquete(formatearFecha(item.getPaquete().getFechaInicio()));
            view.setFechaFinPaquete(formatearFecha(item.getPaquete().getFechaFin()));
            view.setStockDisponible(item.getPaquete().getStockDisponible());

            if (item.getPaquete().getDestino() != null) {
                view.setNombreDestino(item.getPaquete().getDestino().getNombre());
            }
        }

        view.setServiciosAdicionales(toServicioViewList(item.getServicios()));
        view.setSubtotalFormateado(formatearPrecio(subtotal));

        return view;
    }

    public List<CarritoItemView> toItemViewList(List<CarritoItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemView)
                .toList();
    }

    public CarritoItemServicioView toServicioView(CarritoItemServicio servicio) {
        if (servicio == null) return null;

        BigDecimal costo = servicio.getServicioAdicional() != null ?
            servicio.getServicioAdicional().getCosto() : BigDecimal.ZERO;
        Integer cantidad = servicio.getCantidad() != null ? servicio.getCantidad() : 0;
        BigDecimal subtotal = costo.multiply(BigDecimal.valueOf(cantidad));

        CarritoItemServicioView view = new CarritoItemServicioView();
        view.setIdServicio(servicio.getServicioAdicional() != null ?
            servicio.getServicioAdicional().getIdServicio() : null);
        view.setNombreServicio(servicio.getServicioAdicional() != null ?
            servicio.getServicioAdicional().getNombre() : null);
        view.setCostoUnitarioFormateado(formatearPrecio(costo));
        view.setCantidad(cantidad);
        view.setSubtotalFormateado(formatearPrecio(subtotal));

        return view;
    }

    public List<CarritoItemServicioView> toServicioViewList(List<CarritoItemServicio> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toServicioView)
                .toList();
    }
}
