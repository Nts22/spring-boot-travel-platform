package com.ptirado.nmviajes.service;

import java.util.List;

import com.ptirado.nmviajes.dto.api.response.ServicioAdicionalResponse;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

public interface ServicioAdicionalService {

    // API REST
    List<ServicioAdicionalResponse> listarParaApi();
    List<ServicioAdicionalResponse> listarActivosParaApi();
    ServicioAdicionalResponse obtenerParaApi(Integer id);

    // WEB MVC
    List<ServicioAdicionalView> listarParaWeb();
    List<ServicioAdicionalView> listarActivosParaWeb();
    ServicioAdicionalView obtenerParaWeb(Integer id);
}
