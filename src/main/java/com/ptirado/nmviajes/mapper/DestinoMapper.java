package com.ptirado.nmviajes.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ptirado.nmviajes.dto.api.request.DestinoRequest;
import com.ptirado.nmviajes.dto.api.response.DestinoResponse;
import com.ptirado.nmviajes.dto.form.DestinoForm;
import com.ptirado.nmviajes.entity.Destino;
import com.ptirado.nmviajes.viewmodel.DestinoView;

@Component
public class DestinoMapper {

    // ===========================================================
    //               MAPEOS PARA API (JSON)
    // ===========================================================

    /**
     * Convierte DestinoRequest → DestinoEntity
     * Usado en POST /api/v1/destinos
     */
    public Destino toEntityFromRequest(DestinoRequest request) {
        if (request == null) return null;

        Destino destino = new Destino();
        destino.setNombre(request.getNombre());
        destino.setPais(request.getPais());
        destino.setDescripcion(request.getDescripcion());
        destino.setEstado(request.getEstado());
        return destino;
    }

    /**
     * Convierte DestinoEntity → DestinoResponse
     */
    public DestinoResponse toResponseFromEntity(Destino destino) {
        if (destino == null) return null;

        return DestinoResponse.builder()
                .idDestino(destino.getIdDestino())
                .nombre(destino.getNombre())
                .pais(destino.getPais())
                .descripcion(destino.getDescripcion())
                .estado(destino.getEstado())
                .fechaCreacion(destino.getFechaCreacion())
                .fechaModificacion(destino.getFechaModificacion())
                .build();
    }

    /**
     * Convierte Lista<Destino> → Lista<DestinoResponse>
     */
    public List<DestinoResponse> toResponseList(List<Destino> destinos) {
        if (destinos == null) return List.of();

        return destinos.stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    /**
     * Actualiza una entidad existente desde un DestinoRequest
     * Usado en PUT /api/v1/destinos/{id}
     */
    public void updateEntityFromRequest(DestinoRequest request, Destino destino) {
        if (request == null || destino == null) return;

        destino.setNombre(request.getNombre());
        destino.setPais(request.getPais());
        destino.setDescripcion(request.getDescripcion());
        destino.setEstado(request.getEstado());
    }

    // ===========================================================
    //               MAPEOS PARA WEB MVC (THYMELEAF)
    // ===========================================================

    /**
     * Convierte DestinoEntity → DestinoForm
     * Usado en: /admin/destinos/editar/{id}
     */
    public DestinoForm toFormFromEntity(Destino destino) {
        if (destino == null) return null;

        return DestinoForm.builder()
                .idDestino(destino.getIdDestino())
                .nombre(destino.getNombre())
                .pais(destino.getPais())
                .descripcion(destino.getDescripcion())
                .estado(destino.getEstado())
                .build();
    }

    /**
     * Convierte DestinoForm → DestinoEntity
     * Usado en creación desde vista web
     */
    public Destino toEntityFromForm(DestinoForm form) {
        if (form == null) return null;

        Destino destino = new Destino();
        destino.setNombre(form.getNombre());
        destino.setPais(form.getPais());
        destino.setDescripcion(form.getDescripcion());
        destino.setEstado(form.getEstado());
        return destino;
    }

    /**
     * Convierte Entity → ViewModel (para mostrar en tablas)
     */
    public DestinoView toViewModelFromEntity(Destino destino) {
        if (destino == null) return null;

        DestinoView vm = new DestinoView();
        vm.setIdDestino(destino.getIdDestino());
        vm.setNombre(destino.getNombre());
        vm.setPais(destino.getPais());
        vm.setDescripcion(destino.getDescripcion());
        vm.setEstado(destino.getEstado());

        vm.setFechaCreacionFormateada(
                destino.getFechaCreacion() != null
                        ? destino.getFechaCreacion().toString()
                        : "-"
        );

        vm.setFechaModificacionFormateada(
                destino.getFechaModificacion() != null
                        ? destino.getFechaModificacion().toString()
                        : "-"
        );

        return vm;
    }

    /**
     * Lista de Entity → Lista de ViewModel
     */
    public List<DestinoView> toViewList(List<Destino> destinos) {
        if (destinos == null) return List.of();

        return destinos.stream()
                .map(this::toViewModelFromEntity)
                .toList();
    }

    /**
     * Actualiza una entidad existente desde un formulario web
     */
    public void updateEntityFromForm(DestinoForm form, Destino destino) {
        if (form == null || destino == null) return;

        destino.setNombre(form.getNombre());
        destino.setPais(form.getPais());
        destino.setDescripcion(form.getDescripcion());
        destino.setEstado(form.getEstado());
    }
}
