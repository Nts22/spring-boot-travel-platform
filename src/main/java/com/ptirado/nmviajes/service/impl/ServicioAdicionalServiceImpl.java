package com.ptirado.nmviajes.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.AppConstants;
import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.response.ServicioAdicionalResponse;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.ServicioAdicionalMapper;
import com.ptirado.nmviajes.repository.ServicioAdicionalRepository;
import com.ptirado.nmviajes.service.ServicioAdicionalService;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ServicioAdicionalServiceImpl implements ServicioAdicionalService {

    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ServicioAdicionalMapper servicioAdicionalMapper;

    private ServicioAdicional getServicioOrThrow(Integer id) {
        return servicioAdicionalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.SERVICIO_NOT_FOUND, id));
    }

    // ===========================================================
    // API REST
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ServicioAdicionalResponse> listarParaApi() {
        return servicioAdicionalMapper.toResponseList(servicioAdicionalRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioAdicionalResponse> listarActivosParaApi() {
        return servicioAdicionalMapper.toResponseList(
            servicioAdicionalRepository.findByEstado(AppConstants.STATUS_ACTIVO));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioAdicionalResponse obtenerParaApi(Integer id) {
        return servicioAdicionalMapper.toResponseFromEntity(getServicioOrThrow(id));
    }

    // ===========================================================
    // WEB MVC
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ServicioAdicionalView> listarParaWeb() {
        return servicioAdicionalMapper.toViewList(servicioAdicionalRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioAdicionalView> listarActivosParaWeb() {
        return servicioAdicionalMapper.toViewList(
            servicioAdicionalRepository.findByEstado(AppConstants.STATUS_ACTIVO));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioAdicionalView obtenerParaWeb(Integer id) {
        return servicioAdicionalMapper.toViewModelFromEntity(getServicioOrThrow(id));
    }
}
