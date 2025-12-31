package com.ptirado.nmviajes.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.request.ContactoRequest;
import com.ptirado.nmviajes.service.ContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.CONTACTO)
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @PostMapping
    public ResponseEntity<Void> enviarContacto(@Valid @RequestBody ContactoRequest request) {
        contactoService.procesarContacto(request);
        return ResponseEntity.ok().build();
    }
}