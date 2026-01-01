package com.ptirado.nmviajes.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.AppConstants;
import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.request.PaqueteRequest;
import com.ptirado.nmviajes.dto.api.response.PaqueteBuscadorResponse;
import com.ptirado.nmviajes.dto.api.response.PaqueteResponse;
import com.ptirado.nmviajes.dto.form.PaqueteForm;
import com.ptirado.nmviajes.entity.Destino;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.PaqueteMapper;
import com.ptirado.nmviajes.repository.DestinoRepository;
import com.ptirado.nmviajes.repository.PaqueteRepository;
import com.ptirado.nmviajes.service.PaqueteService;
import com.ptirado.nmviajes.viewmodel.PaqueteView;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaqueteServiceImpl implements PaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final DestinoRepository destinoRepository;
    private final PaqueteMapper paqueteMapper;

    // ===========================================================
    // UTILIDAD INTERNA
    // ===========================================================

    private Paquete getPaqueteOrThrow(Integer id) {
        return paqueteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PAQUETE_NOT_FOUND, id));
    }

    private Destino getDestinoOrThrow(Integer idDestino) {
        return destinoRepository.findById(idDestino)
                .orElseThrow(() -> new NotFoundException(MessageKeys.DESTINO_NOT_FOUND, idDestino));
    }

    // ===========================================================
    // API REST
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteResponse> listarParaApi() {
        return paqueteMapper.toResponseList(paqueteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PaqueteResponse obtenerParaApi(Integer id) {
        return paqueteMapper.toResponseFromEntity(getPaqueteOrThrow(id));
    }

    @Override
    public PaqueteResponse crearDesdeApi(PaqueteRequest request) {
        Destino destino = getDestinoOrThrow(request.getIdDestino());
        Paquete entity = paqueteMapper.toEntityFromRequest(request, destino);
        Paquete saved = paqueteRepository.save(entity);
        return paqueteMapper.toResponseFromEntity(saved);
    }

    @Override
    public PaqueteResponse actualizarDesdeApi(Integer id, PaqueteRequest request) {
        Paquete paqueteDb = getPaqueteOrThrow(id);
        Destino destino = getDestinoOrThrow(request.getIdDestino());
        paqueteMapper.updateEntityFromRequest(request, paqueteDb, destino);
        return paqueteMapper.toResponseFromEntity(paqueteRepository.save(paqueteDb));
    }

    @Override
    public void eliminar(Integer id) {
        Paquete paquete = getPaqueteOrThrow(id);
        paqueteRepository.delete(paquete);
    }

    // ===========================================================
    // WEB MVC
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteView> listarParaWeb() {
        return paqueteMapper.toViewList(paqueteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PaqueteView obtenerParaWeb(Integer id) {
        return paqueteMapper.toViewModelFromEntity(getPaqueteOrThrow(id));
    }

    @Override
    public void crearDesdeForm(PaqueteForm form) {
        Destino destino = getDestinoOrThrow(form.getIdDestino());
        Paquete entity = paqueteMapper.toEntityFromForm(form, destino);
        paqueteRepository.save(entity);
    }

    @Override
    public void actualizarDesdeForm(Integer id, PaqueteForm form) {
        Paquete paqueteDb = getPaqueteOrThrow(id);
        Destino destino = getDestinoOrThrow(form.getIdDestino());
        paqueteMapper.updateEntityFromForm(form, paqueteDb, destino);
        paqueteRepository.save(paqueteDb);
    }

    // ===========================================================
    // BÚSQUEDAS ADICIONALES
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteResponse> listarPorDestino(Integer idDestino) {
        List<Paquete> paquetes = paqueteRepository.findByDestino_IdDestino(idDestino);
        return paqueteMapper.toResponseList(paquetes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteResponse> listarActivos() {
        List<Paquete> paquetes = paqueteRepository.findByEstado(AppConstants.STATUS_ACTIVO);
        return paqueteMapper.toResponseList(paquetes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteBuscadorResponse> buscar(Integer idDestino, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Paquete> paquetes = paqueteRepository.buscar(idDestino, fechaInicio, fechaFin);
        return paqueteMapper.toBuscadorResponseList(paquetes);
    }
}