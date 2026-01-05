package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.config.FormatConfig;
import com.ptirado.nmviajes.dto.api.response.ReservaItemResponse;
import com.ptirado.nmviajes.dto.api.response.ReservaItemServicioResponse;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;
import com.ptirado.nmviajes.entity.ReservaItem;
import com.ptirado.nmviajes.entity.ReservaItemServicio;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.entity.Usuario;
import com.ptirado.nmviajes.viewmodel.ReservaItemServicioView;
import com.ptirado.nmviajes.viewmodel.ReservaItemView;
import com.ptirado.nmviajes.viewmodel.ReservaView;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entidades de Reserva a DTOs y ViewModels.
 *
 * <p>Este mapper maneja las conversiones entre:</p>
 * <ul>
 *   <li><b>Entidad → Response:</b> Para API REST (datos crudos en JSON)</li>
 *   <li><b>Entidad → ViewModel:</b> Para vistas Thymeleaf (datos formateados)</li>
 * </ul>
 *
 * <h3>Diferencias clave:</h3>
 * <table border="1">
 *   <tr><th>Response (API)</th><th>ViewModel (Web)</th></tr>
 *   <tr><td>BigDecimal precio</td><td>String precioFormateado ("S/ 1,500.00")</td></tr>
 *   <tr><td>LocalDate fecha</td><td>String fechaFormateada ("15/01/2026")</td></tr>
 *   <tr><td>EstadoReserva.PENDIENTE</td><td>"Pendiente de Pago"</td></tr>
 * </table>
 *
 * @author Sistema NMViajes
 * @see FormatConfig
 */
@Component
@RequiredArgsConstructor
public class ReservaMapper {

    private final FormatConfig formatConfig;

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                      METODOS AUXILIARES DE FORMATEO                        ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Convierte el estado de la reserva a texto legible para el usuario.
     *
     * @param estado Estado del enum
     * @return Texto descriptivo del estado
     */
    private String formatearEstado(EstadoReserva estado) {
        if (estado == null) {
            return "Desconocido";
        }
        return switch (estado) {
            case PENDIENTE -> "Pendiente de Pago";
            case PAGADA -> "Pagada (Finalizada)";
            case CANCELADA -> "Cancelada";
        };
    }

    /**
     * Calcula el subtotal de un servicio adicional (costo x cantidad).
     */
    private BigDecimal calcularSubtotalServicio(BigDecimal costoUnitario, Integer cantidad) {
        if (costoUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return costoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                    METODOS AUXILIARES DE EXTRACCION                        ║
    // ║  Extraen datos de entidades relacionadas de forma segura (null-safe)      ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    private Integer getUsuarioId(Usuario usuario) {
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    private String getNombreCompletoUsuario(Usuario usuario) {
        return usuario != null
                ? usuario.getNombre() + " " + usuario.getApellido()
                : null;
    }

    private String getEmailUsuario(Usuario usuario) {
        return usuario != null ? usuario.getEmail() : null;
    }

    private Integer getPaqueteId(Paquete paquete) {
        return paquete != null ? paquete.getIdPaquete() : null;
    }

    private String getNombrePaquete(Paquete paquete) {
        return paquete != null ? paquete.getNombre() : null;
    }

    private BigDecimal getPrecioPaquete(Paquete paquete) {
        return paquete != null ? paquete.getPrecio() : null;
    }

    private String getNombreDestino(Paquete paquete) {
        return paquete != null && paquete.getDestino() != null
                ? paquete.getDestino().getNombre()
                : null;
    }

    private Integer getServicioId(ServicioAdicional servicio) {
        return servicio != null ? servicio.getIdServicio() : null;
    }

    private String getNombreServicio(ServicioAdicional servicio) {
        return servicio != null ? servicio.getNombre() : null;
    }

    private BigDecimal getCostoServicio(ServicioAdicional servicio) {
        return servicio != null ? servicio.getCosto() : BigDecimal.ZERO;
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         MAPEOS PARA API REST                               ║
    // ║  Retornan DTOs con datos crudos para serializar a JSON                    ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Convierte una entidad Reserva a ReservaResponse para la API.
     *
     * @param reserva Entidad de reserva
     * @return DTO con datos de la reserva
     */
    public ReservaResponse toResponseFromEntity(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        Usuario usuario = reserva.getUsuario();

        return ReservaResponse.builder()
                .idReserva(reserva.getIdReserva())
                .totalPagar(reserva.getTotalPagar())
                .estadoReserva(reserva.getEstadoReserva() != null
                        ? reserva.getEstadoReserva().name()
                        : null)
                .finalizada(reserva.estaFinalizada())
                .fechaCreacion(reserva.getFechaCreacion())
                .idUsuario(getUsuarioId(usuario))
                .nombreUsuario(getNombreCompletoUsuario(usuario))
                .emailUsuario(getEmailUsuario(usuario))
                .items(toItemResponseList(reserva.getItems()))
                .build();
    }

    /**
     * Convierte una lista de reservas a lista de responses.
     */
    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if (reservas == null) {
            return List.of();
        }
        return reservas.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    /**
     * Convierte un item de reserva a response.
     */
    public ReservaItemResponse toItemResponse(ReservaItem item) {
        if (item == null) {
            return null;
        }

        Paquete paquete = item.getPaquete();

        return ReservaItemResponse.builder()
                .idItem(item.getIdItem())
                .fechaViajeInicio(item.getFechaViajeInicio())
                .subtotal(item.getSubtotal())
                .idPaquete(getPaqueteId(paquete))
                .nombrePaquete(getNombrePaquete(paquete))
                .precioPaquete(getPrecioPaquete(paquete))
                .nombreDestino(getNombreDestino(paquete))
                .serviciosAdicionales(toItemServicioResponseList(item.getServicios()))
                .build();
    }

    public List<ReservaItemResponse> toItemResponseList(List<ReservaItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(this::toItemResponse)
                .toList();
    }

    /**
     * Convierte un servicio de item a response.
     */
    public ReservaItemServicioResponse toItemServicioResponse(ReservaItemServicio itemServicio) {
        if (itemServicio == null) {
            return null;
        }

        ServicioAdicional servicio = itemServicio.getServicioAdicional();
        BigDecimal costoUnitario = getCostoServicio(servicio);
        Integer cantidad = itemServicio.getCantidad() != null ? itemServicio.getCantidad() : 0;
        BigDecimal subtotal = calcularSubtotalServicio(costoUnitario, cantidad);

        return ReservaItemServicioResponse.builder()
                .idServicio(getServicioId(servicio))
                .nombreServicio(getNombreServicio(servicio))
                .costoUnitario(costoUnitario)
                .cantidad(cantidad)
                .subtotal(subtotal)
                .build();
    }

    public List<ReservaItemServicioResponse> toItemServicioResponseList(List<ReservaItemServicio> servicios) {
        if (servicios == null) {
            return List.of();
        }
        return servicios.stream()
                .map(this::toItemServicioResponse)
                .toList();
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                        MAPEOS PARA WEB (THYMELEAF)                         ║
    // ║  Retornan ViewModels con datos formateados para mostrar en vistas         ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Convierte una entidad Reserva a ReservaView para la vista web.
     *
     * <p>Los datos numericos y fechas se formatean para mostrar al usuario.</p>
     *
     * @param reserva Entidad de reserva
     * @return ViewModel con datos formateados
     */
    public ReservaView toViewModelFromEntity(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        Usuario usuario = reserva.getUsuario();
        EstadoReserva estado = reserva.getEstadoReserva();

        ReservaView view = new ReservaView();
        view.setIdReserva(reserva.getIdReserva());
        view.setTotalPagarFormateado(formatConfig.formatearPrecio(reserva.getTotalPagar()));
        view.setEstadoReserva(formatearEstado(estado));
        view.setEstadoReservaCode(estado != null ? estado.name() : null);
        view.setFinalizada(reserva.estaFinalizada());
        view.setCancelada(estado == EstadoReserva.CANCELADA);
        view.setFechaCreacionFormateada(formatConfig.formatearFechaHora(reserva.getFechaCreacion()));

        // Datos del usuario
        view.setIdUsuario(getUsuarioId(usuario));
        view.setNombreCompletoUsuario(getNombreCompletoUsuario(usuario));
        view.setEmailUsuario(getEmailUsuario(usuario));

        // Items de la reserva
        view.setItems(toItemViewList(reserva.getItems()));

        return view;
    }

    /**
     * Convierte una lista de reservas a lista de views.
     */
    public List<ReservaView> toViewList(List<Reserva> reservas) {
        if (reservas == null) {
            return List.of();
        }
        return reservas.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }

    /**
     * Convierte un item de reserva a view.
     */
    public ReservaItemView toItemView(ReservaItem item) {
        if (item == null) {
            return null;
        }

        Paquete paquete = item.getPaquete();

        ReservaItemView view = new ReservaItemView();
        view.setIdItem(item.getIdItem());
        view.setFechaViajeInicioFormateada(formatConfig.formatearFecha(item.getFechaViajeInicio()));
        view.setSubtotalFormateado(formatConfig.formatearPrecio(item.getSubtotal()));

        // Datos del paquete
        if (paquete != null) {
            view.setIdPaquete(paquete.getIdPaquete());
            view.setNombrePaquete(paquete.getNombre());
            view.setPrecioPaqueteFormateado(formatConfig.formatearPrecio(paquete.getPrecio()));
            view.setNombreDestino(getNombreDestino(paquete));
        }

        // Servicios adicionales
        view.setServiciosAdicionales(toItemServicioViewList(item.getServicios()));

        return view;
    }

    public List<ReservaItemView> toItemViewList(List<ReservaItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(this::toItemView)
                .toList();
    }

    /**
     * Convierte un servicio de item a view.
     */
    public ReservaItemServicioView toItemServicioView(ReservaItemServicio itemServicio) {
        if (itemServicio == null) {
            return null;
        }

        ServicioAdicional servicio = itemServicio.getServicioAdicional();
        BigDecimal costoUnitario = getCostoServicio(servicio);
        Integer cantidad = itemServicio.getCantidad() != null ? itemServicio.getCantidad() : 0;
        BigDecimal subtotal = calcularSubtotalServicio(costoUnitario, cantidad);

        ReservaItemServicioView view = new ReservaItemServicioView();
        view.setIdServicio(getServicioId(servicio));
        view.setNombreServicio(getNombreServicio(servicio));
        view.setCostoUnitarioFormateado(formatConfig.formatearPrecio(costoUnitario));
        view.setCantidad(cantidad);
        view.setSubtotalFormateado(formatConfig.formatearPrecio(subtotal));

        return view;
    }

    public List<ReservaItemServicioView> toItemServicioViewList(List<ReservaItemServicio> servicios) {
        if (servicios == null) {
            return List.of();
        }
        return servicios.stream()
                .map(this::toItemServicioView)
                .toList();
    }
}
