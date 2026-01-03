package com.ptirado.nmviajes.service;

import java.util.List;

import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.viewmodel.ReservaView;

public interface ReservaService {

    // ===========================================================
    // API REST
    // ===========================================================

    List<ReservaResponse> listarParaApi();

    ReservaResponse obtenerParaApi(Integer id);

    ReservaResponse crearDesdeApi(ReservaRequest request);

    List<ReservaResponse> listarPorUsuarioParaApi(Integer idUsuario);

    // ===========================================================
    // WEB MVC
    // ===========================================================

    List<ReservaView> listarParaWeb();

    ReservaView obtenerParaWeb(Integer id);

    void crearDesdeForm(ReservaForm form);

    List<ReservaView> listarPorUsuarioParaWeb(Integer idUsuario);

    // ===========================================================
    // PAGO / FINALIZACIÓN
    // ===========================================================

    ReservaResponse confirmarPago(Integer idReserva);

    ReservaResponse cancelarReserva(Integer idReserva);

    void pagarParaWeb(Integer idReserva);

    void cancelarParaWeb(Integer idReserva);
}
