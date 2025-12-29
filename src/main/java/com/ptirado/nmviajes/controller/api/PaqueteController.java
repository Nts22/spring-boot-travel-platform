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
import com.ptirado.nmviajes.dto.api.request.PaqueteRequest;
import com.ptirado.nmviajes.dto.api.response.PaqueteResponse;
import com.ptirado.nmviajes.service.PaqueteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.PAQUETES)
@RequiredArgsConstructor
public class PaqueteController {

    private final PaqueteService paqueteService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<PaqueteResponse>> listar() {
        return ResponseEntity.ok(paqueteService.listarParaApi());
    }

    // OBTENER POR ID
    @GetMapping(ApiPaths.PAQUETES_ID)
    public ResponseEntity<PaqueteResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(paqueteService.obtenerParaApi(id));
    }

    // CREAR
    @PostMapping
    public ResponseEntity<PaqueteResponse> crear(@Valid @RequestBody PaqueteRequest request) {
        PaqueteResponse response = paqueteService.crearDesdeApi(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ACTUALIZAR
    @PutMapping(ApiPaths.PAQUETES_ID)
    public ResponseEntity<PaqueteResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PaqueteRequest request) {

        PaqueteResponse response = paqueteService.actualizarDesdeApi(id, request);
        return ResponseEntity.ok(response);
    }

    // ELIMINAR
    @DeleteMapping(ApiPaths.PAQUETES_ID)
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        paqueteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // LISTAR POR DESTINO
    @GetMapping("/destino/{idDestino}")
    public ResponseEntity<List<PaqueteResponse>> listarPorDestino(@PathVariable Integer idDestino) {
        return ResponseEntity.ok(paqueteService.listarPorDestino(idDestino));
    }

    // LISTAR ACTIVOS
    @GetMapping("/activos")
    public ResponseEntity<List<PaqueteResponse>> listarActivos() {
        return ResponseEntity.ok(paqueteService.listarActivos());
    }
}
