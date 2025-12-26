package com.ptirado.nmviajes.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.request.DestinoRequest;
import com.ptirado.nmviajes.dto.api.response.DestinoResponse;
import com.ptirado.nmviajes.service.DestinoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.DESTINOS)
@RequiredArgsConstructor
public class DestinoController {

    private final DestinoService destinoService;

    // LISTAR
    @GetMapping
    public ResponseEntity<List<DestinoResponse>> listar() {
        return ResponseEntity.ok(destinoService.listarParaApi());
    }

    // OBTENER POR ID
    @GetMapping(ApiPaths.DESTINOS_ID)
    public ResponseEntity<DestinoResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(destinoService.obtenerParaApi(id));
    }

    // CREAR
    @PostMapping
    public ResponseEntity<DestinoResponse> crear(@Valid @RequestBody DestinoRequest request) {
        DestinoResponse response = destinoService.crearDesdeApi(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ACTUALIZAR
    @PutMapping(ApiPaths.DESTINOS_ID)
    public ResponseEntity<DestinoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DestinoRequest request) {

        DestinoResponse response = destinoService.actualizarDesdeApi(id, request);
        return ResponseEntity.ok(response);
    }

    // ELIMINAR
    @DeleteMapping(ApiPaths.DESTINOS_ID)
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        destinoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
