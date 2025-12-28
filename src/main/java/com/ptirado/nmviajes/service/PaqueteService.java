package com.ptirado.nmviajes.service;

import java.util.List;

import com.ptirado.nmviajes.dto.api.response.PaqueteResponse;

public interface PaqueteService {

    // API REST
    List<PaqueteResponse> listarParaApi();
    PaqueteResponse obtenerParaApi(Integer id);
    PaqueteResponse crearDesdeApi();
    PaqueteResponse actualizarDesdeApi(Integer id);
    void eliminar(Integer id);

    // WEB
    List<PaqueteResponse> listarParaWeb();
    PaqueteResponse obtenerParaWeb(Integer id);
    void crearDesdeForm();
    void actualizarDesdeForm(Integer id);
}
