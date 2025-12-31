package com.ptirado.nmviajes.service.impl;

import org.springframework.stereotype.Service;

import com.ptirado.nmviajes.dto.api.request.ContactoRequest;
import com.ptirado.nmviajes.service.ContactoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactoServiceImpl implements ContactoService {

    @Override
    public void procesarContacto(ContactoRequest request) {
        // Registrar la solicitud de contacto
        log.info("Nueva solicitud de contacto recibida - Nombre: {}, Email: {}, Telefono: {}",
                request.getNombre(),
                request.getEmail(),
                request.getTelefono() != null ? request.getTelefono() : "No proporcionado");

        log.debug("Mensaje de contacto: {}", request.getMensaje());

        // TODO: Implementar envio de email cuando se configure el servicio de correo
        // TODO: Opcionalmente guardar en tabla de contactos para seguimiento
    }
}