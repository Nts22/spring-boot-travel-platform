package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.request.PaqueteRequest;
import com.ptirado.nmviajes.dto.api.response.PaqueteBuscadorResponse;
import com.ptirado.nmviajes.dto.api.response.PaqueteResponse;
import com.ptirado.nmviajes.dto.form.PaqueteForm;
import com.ptirado.nmviajes.entity.Destino;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.viewmodel.PaqueteView;

@Component
public class PaqueteMapper {

    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

    private String formatearPrecio(BigDecimal precio) {
        return precio != null ? PRICE_FORMATTER.format(precio) : "0.00";
    }

    // ===========================================================
    //               MAPEOS PARA API (JSON)
    // ===========================================================

    /**
     * Convierte PaqueteRequest → PaqueteEntity
     * Usado en POST /api/v1/paquetes
     */
    public Paquete toEntityFromRequest(PaqueteRequest request, Destino destino) {
        if (request == null) return null;

        Paquete paquete = new Paquete();
        paquete.setNombre(request.getNombre());
        paquete.setDescripcion(request.getDescripcion());
        paquete.setPrecio(request.getPrecio());
        paquete.setFechaInicio(request.getFechaInicio());
        paquete.setFechaFin(request.getFechaFin());
        paquete.setStockDisponible(request.getStockDisponible());
        paquete.setEstado(request.getEstado());
        paquete.setDestino(destino);
        return paquete;
    }

    /**
     * Convierte PaqueteEntity → PaqueteResponse
     */
    public PaqueteResponse toResponseFromEntity(Paquete paquete) {
        if (paquete == null) return null;

        return PaqueteResponse.builder()
                .idPaquete(paquete.getIdPaquete())
                .nombre(paquete.getNombre())
                .descripcion(paquete.getDescripcion())
                .precio(paquete.getPrecio())
                .fechaInicio(paquete.getFechaInicio())
                .fechaFin(paquete.getFechaFin())
                .stockDisponible(paquete.getStockDisponible())
                .estado(paquete.getEstado())
                .idDestino(paquete.getDestino() != null ? paquete.getDestino().getIdDestino() : null)
                .nombreDestino(paquete.getDestino() != null ? paquete.getDestino().getNombre() : null)
                .fechaCreacion(paquete.getFechaCreacion())
                .fechaModificacion(paquete.getFechaModificacion())
                .build();
    }

    /**
     * Convierte Lista<Paquete> → Lista<PaqueteResponse>
     */
    public List<PaqueteResponse> toResponseList(List<Paquete> paquetes) {
        if (paquetes == null) return List.of();

        return paquetes.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    /**
     * Actualiza una entidad existente desde un PaqueteRequest
     * Usado en PUT /api/v1/paquetes/{id}
     */
    public void updateEntityFromRequest(PaqueteRequest request, Paquete paquete, Destino destino) {
        if (request == null || paquete == null) return;

        paquete.setNombre(request.getNombre());
        paquete.setDescripcion(request.getDescripcion());
        paquete.setPrecio(request.getPrecio());
        paquete.setFechaInicio(request.getFechaInicio());
        paquete.setFechaFin(request.getFechaFin());
        paquete.setStockDisponible(request.getStockDisponible());
        paquete.setEstado(request.getEstado());
        paquete.setDestino(destino);
    }

    /**
     * Convierte Entity → PaqueteBuscadorResponse (datos formateados para JS)
     */
    public PaqueteBuscadorResponse toBuscadorResponse(Paquete paquete) {
        if (paquete == null) return null;

        return PaqueteBuscadorResponse.builder()
                .idPaquete(paquete.getIdPaquete())
                .nombre(paquete.getNombre())
                .descripcion(paquete.getDescripcion())
                .precio(formatearPrecio(paquete.getPrecio()))
                .fechaInicio(formatearFecha(paquete.getFechaInicio()))
                .fechaFin(formatearFecha(paquete.getFechaFin()))
                .stockDisponible(paquete.getStockDisponible())
                .nombreDestino(paquete.getDestino() != null ? paquete.getDestino().getNombre() : null)
                .build();
    }

    /**
     * Lista de Entity → Lista de PaqueteBuscadorResponse
     */
    public List<PaqueteBuscadorResponse> toBuscadorResponseList(List<Paquete> paquetes) {
        if (paquetes == null) return List.of();

        return paquetes.stream()
                .map(this::toBuscadorResponse)
                .toList();
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    /**
     * Convierte PaqueteEntity → PaqueteForm
     * Usado en: /admin/paquetes/editar/{id}
     */
    public PaqueteForm toFormFromEntity(Paquete paquete) {
        if (paquete == null) return null;

        return PaqueteForm.builder()
                .idPaquete(paquete.getIdPaquete())
                .nombre(paquete.getNombre())
                .descripcion(paquete.getDescripcion())
                .precio(paquete.getPrecio())
                .fechaInicio(paquete.getFechaInicio())
                .fechaFin(paquete.getFechaFin())
                .stockDisponible(paquete.getStockDisponible())
                .estado(paquete.getEstado())
                .idDestino(paquete.getDestino() != null ? paquete.getDestino().getIdDestino() : null)
                .build();
    }

    /**
     * Convierte PaqueteForm → PaqueteEntity
     * Usado en creación desde vista web
     */
    public Paquete toEntityFromForm(PaqueteForm form, Destino destino) {
        if (form == null) return null;

        Paquete paquete = new Paquete();
        paquete.setNombre(form.getNombre());
        paquete.setDescripcion(form.getDescripcion());
        paquete.setPrecio(form.getPrecio());
        paquete.setFechaInicio(form.getFechaInicio());
        paquete.setFechaFin(form.getFechaFin());
        paquete.setStockDisponible(form.getStockDisponible());
        paquete.setEstado(form.getEstado());
        paquete.setDestino(destino);
        return paquete;
    }

    /**
     * Convierte Entity → ViewModel (para mostrar en tablas)
     */
    public PaqueteView toViewModelFromEntity(Paquete paquete) {
        if (paquete == null) return null;

        PaqueteView vm = new PaqueteView();
        vm.setIdPaquete(paquete.getIdPaquete());
        vm.setNombre(paquete.getNombre());
        vm.setDescripcion(paquete.getDescripcion());
        vm.setPrecio(paquete.getPrecio() != null ? paquete.getPrecio().doubleValue() : null);
        vm.setStockDisponible(paquete.getStockDisponible() != null ? paquete.getStockDisponible().toString() : null);
        vm.setEstado(paquete.getEstado());
        vm.setIdDestino(paquete.getDestino() != null ? paquete.getDestino().getIdDestino() : null);
        vm.setNombreDestino(paquete.getDestino() != null ? paquete.getDestino().getNombre() : null);

        vm.setFechaInicioFormateada(
                paquete.getFechaInicio() != null
                        ? paquete.getFechaInicio().toString()
                        : "-"
        );

        vm.setFechaFinFormateada(
                paquete.getFechaFin() != null
                        ? paquete.getFechaFin().toString()
                        : "-"
        );

        vm.setFechaCreacionFormateada(
                paquete.getFechaCreacion() != null
                        ? paquete.getFechaCreacion().toString()
                        : "-"
        );

        vm.setFechaModificacionFormateada(
                paquete.getFechaModificacion() != null
                        ? paquete.getFechaModificacion().toString()
                        : "-"
        );

        return vm;
    }

    /**
     * Lista de Entity → Lista de ViewModel
     */
    public List<PaqueteView> toViewList(List<Paquete> paquetes) {
        if (paquetes == null) return List.of();

        return paquetes.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }

    /**
     * Actualiza una entidad existente desde un formulario web
     */
    public void updateEntityFromForm(PaqueteForm form, Paquete paquete, Destino destino) {
        if (form == null || paquete == null) return;

        paquete.setNombre(form.getNombre());
        paquete.setDescripcion(form.getDescripcion());
        paquete.setPrecio(form.getPrecio());
        paquete.setFechaInicio(form.getFechaInicio());
        paquete.setFechaFin(form.getFechaFin());
        paquete.setStockDisponible(form.getStockDisponible());
        paquete.setEstado(form.getEstado());
        paquete.setDestino(destino);
    }
}