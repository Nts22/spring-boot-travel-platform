package com.ptirado.nmviajes.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.request.DestinoRequest;
import com.ptirado.nmviajes.dto.api.response.DestinoResponse;
import com.ptirado.nmviajes.dto.form.DestinoForm;
import com.ptirado.nmviajes.entity.Destino;
import com.ptirado.nmviajes.exception.api.ConflictException;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.DestinoMapper;
import com.ptirado.nmviajes.repository.DestinoRepository;
import com.ptirado.nmviajes.service.DestinoService;
import com.ptirado.nmviajes.util.MessageUtils;
import com.ptirado.nmviajes.viewmodel.DestinoView;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DestinoServiceImpl implements DestinoService {

    private final DestinoRepository destinoRepository;
    private final DestinoMapper destinoMapper;
    private final MessageUtils message;

    // ===========================================================
    // UTILIDAD INTERNA (solo este servicio la usa)
    // ===========================================================
    private Destino getDestinoOrThrow(Integer id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.DESTINO_NOT_FOUND, id));
    }

    private void validarNombreUnico(String nombre, Integer idActual) {

        Optional<Destino> optional = destinoRepository.findByNombre(nombre);

        if (optional.isEmpty())
            return; // no existe → OK
        Destino existente = optional.get();
        if (idActual == null || !existente.getIdDestino().equals(idActual)) {
            throw new ConflictException(MessageKeys.DESTINO_DUPLICATE);
        }
    }

    // ===========================================================
    // API REST
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<DestinoResponse> listarParaApi() {
        return destinoMapper.toResponseList(destinoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public DestinoResponse obtenerParaApi(Integer id) {
        return destinoMapper.toResponseFromEntity(getDestinoOrThrow(id));
    }

    @Override
    public DestinoResponse crearDesdeApi(DestinoRequest request) {

        validarNombreUnico(request.getNombre(), null);
        Destino entity = destinoMapper.toEntityFromRequest(request);
        Destino saved = destinoRepository.save(entity);
        return destinoMapper.toResponseFromEntity(saved);
    }

    @Override
    public DestinoResponse actualizarDesdeApi(Integer id, DestinoRequest request) {

        Destino destinoDb = getDestinoOrThrow(id);
        String actual = destinoDb.getNombre().trim().toLowerCase();
        String nuevo = request.getNombre().trim().toLowerCase();

        if (!actual.equals(nuevo)) {
            destinoRepository.findByNombre(request.getNombre().trim())
                    .ifPresent(existente -> {
                        if (!existente.getIdDestino().equals(id)) {
                            throw new ConflictException(message.getMessage(MessageKeys.DESTINO_DUPLICATE, id));
                        }
                    });
        }
        destinoMapper.updateEntityFromRequest(request, destinoDb);
        return destinoMapper.toResponseFromEntity(destinoRepository.save(destinoDb));
    }

    @Override
    public void eliminar(Integer id) {
        Destino destino = getDestinoOrThrow(id);
        destinoRepository.delete(destino);
    }

    // ===========================================================
    // WEB MVC
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<DestinoView> listarParaWeb() {
        return destinoMapper.toViewList(destinoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public DestinoView obtenerParaWeb(Integer id) {
        return destinoMapper.toViewModelFromEntity(getDestinoOrThrow(id));
    }

    @Override
    public void crearDesdeForm(DestinoForm form) {

        validarNombreUnico(form.getNombre(), null);
        Destino entity = destinoMapper.toEntityFromForm(form);
        destinoRepository.save(entity);
    }

    @Override
    public void actualizarDesdeForm(Integer id, DestinoForm form) {

        Destino destinoDb = getDestinoOrThrow(id);
        String actual = destinoDb.getNombre().trim().toLowerCase();
        String nuevo = form.getNombre().trim().toLowerCase();
        if (!actual.equals(nuevo)) {
            destinoRepository.findByNombre(form.getNombre().trim())
                    .ifPresent(existente -> {
                        if (!existente.getIdDestino().equals(id)) {
                            throw new ConflictException(message.getMessage(MessageKeys.DESTINO_DUPLICATE));
                        }
                    });
        }
        destinoMapper.updateEntityFromForm(form, destinoDb);
        destinoRepository.save(destinoDb);
    }

}