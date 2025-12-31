package com.ptirado.nmviajes.service;

import com.ptirado.nmviajes.dto.api.request.ContactoRequest;

public interface ContactoService {

    /**
     * Procesa una solicitud de contacto del formulario web.
     * Por ahora solo registra en log, pero puede extenderse para:
     * - Enviar email
     * - Guardar en base de datos
     * - Notificar via webhook
     */
    void procesarContacto(ContactoRequest request);
}
