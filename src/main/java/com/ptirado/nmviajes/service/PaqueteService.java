package com.ptirado.nmviajes.service;

import java.time.LocalDate;
import java.util.List;

import com.ptirado.nmviajes.dto.api.request.PaqueteRequest;
import com.ptirado.nmviajes.dto.api.response.PaqueteBuscadorResponse;
import com.ptirado.nmviajes.dto.api.response.PaqueteResponse;
import com.ptirado.nmviajes.dto.form.PaqueteForm;
import com.ptirado.nmviajes.viewmodel.PaqueteView;

public interface PaqueteService {

    // API REST
    List<PaqueteResponse> listarParaApi();
    PaqueteResponse obtenerParaApi(Integer id);
    PaqueteResponse crearDesdeApi(PaqueteRequest request);
    PaqueteResponse actualizarDesdeApi(Integer id, PaqueteRequest request);
    void eliminar(Integer id);

    // WEB
    List<PaqueteView> listarParaWeb();
    PaqueteView obtenerParaWeb(Integer id);
    void crearDesdeForm(PaqueteForm form);
    void actualizarDesdeForm(Integer id, PaqueteForm form);

    // Búsquedas adicionales
    List<PaqueteResponse> listarPorDestino(Integer idDestino);
    List<PaqueteResponse> listarActivos();
    List<PaqueteBuscadorResponse> buscar(Integer idDestino, LocalDate fechaInicio, LocalDate fechaFin);
}