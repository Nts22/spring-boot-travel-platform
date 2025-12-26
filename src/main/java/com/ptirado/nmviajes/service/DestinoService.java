package com.ptirado.nmviajes.service;

import java.util.List;

import com.ptirado.nmviajes.dto.api.request.DestinoRequest;
import com.ptirado.nmviajes.dto.api.response.DestinoResponse;
import com.ptirado.nmviajes.dto.form.DestinoForm;
import com.ptirado.nmviajes.viewmodel.DestinoView;

public interface DestinoService {

    // API REST
    List<DestinoResponse> listarParaApi();
    DestinoResponse obtenerParaApi(Integer id);
    DestinoResponse crearDesdeApi(DestinoRequest request);
    DestinoResponse actualizarDesdeApi(Integer id, DestinoRequest request);
    void eliminar(Integer id);

    // WEB
    List<DestinoView> listarParaWeb();
    DestinoView obtenerParaWeb(Integer id);
    void crearDesdeForm(DestinoForm form);
    void actualizarDesdeForm(Integer id, DestinoForm form);
}
