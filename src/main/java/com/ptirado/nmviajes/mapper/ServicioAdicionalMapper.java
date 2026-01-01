package com.ptirado.nmviajes.mapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.response.ServicioAdicionalResponse;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

@Component
public class ServicioAdicionalMapper {

    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance(LOCALE_PE);

    static {
        PRICE_FORMATTER.setMinimumFractionDigits(2);
        PRICE_FORMATTER.setMaximumFractionDigits(2);
    }

    private String formatearPrecio(BigDecimal precio) {
        return precio != null ? "S/ " + PRICE_FORMATTER.format(precio) : "S/ 0.00";
    }

    // ===========================================================
    //               MAPEOS PARA API (JSON)
    // ===========================================================

    public ServicioAdicionalResponse toResponseFromEntity(ServicioAdicional servicio) {
        if (servicio == null) return null;

        return ServicioAdicionalResponse.builder()
                .idServicio(servicio.getIdServicio())
                .nombre(servicio.getNombre())
                .costo(servicio.getCosto())
                .estado(servicio.getEstado())
                .build();
    }

    public List<ServicioAdicionalResponse> toResponseList(List<ServicioAdicional> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    public ServicioAdicionalView toViewModelFromEntity(ServicioAdicional servicio) {
        if (servicio == null) return null;

        ServicioAdicionalView view = new ServicioAdicionalView();
        view.setIdServicio(servicio.getIdServicio());
        view.setNombre(servicio.getNombre());
        view.setCostoFormateado(formatearPrecio(servicio.getCosto()));
        view.setEstado(servicio.getEstado());
        return view;
    }

    public List<ServicioAdicionalView> toViewList(List<ServicioAdicional> servicios) {
        if (servicios == null) return List.of();
        return servicios.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }
}
