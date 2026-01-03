package com.ptirado.nmviajes.service;

import com.ptirado.nmviajes.dto.api.request.CarritoItemRequest;
import com.ptirado.nmviajes.dto.api.response.CarritoResponse;
import com.ptirado.nmviajes.viewmodel.CarritoView;

public interface CarritoService {

    // ===========================================================
    // API REST
    // ===========================================================

    CarritoResponse obtenerCarritoParaApi(Integer idUsuario);

    CarritoResponse agregarItemParaApi(Integer idUsuario, CarritoItemRequest request);

    CarritoResponse eliminarItemParaApi(Integer idUsuario, Integer idItem);

    void vaciarCarritoParaApi(Integer idUsuario);

    void procesarCompraParaApi(Integer idUsuario);

    Integer contarItemsParaApi(Integer idUsuario);

    // ===========================================================
    // WEB MVC
    // ===========================================================

    CarritoView obtenerCarritoParaWeb(Integer idUsuario);

    void agregarItemParaWeb(Integer idUsuario, CarritoItemRequest request);

    void eliminarItemParaWeb(Integer idUsuario, Integer idItem);

    void vaciarCarritoParaWeb(Integer idUsuario);

    void procesarCompraParaWeb(Integer idUsuario);

    Integer contarItemsParaWeb(Integer idUsuario);
}
