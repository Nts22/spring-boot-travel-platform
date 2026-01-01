package com.ptirado.nmviajes.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.response.ServicioAdicionalResponse;
import com.ptirado.nmviajes.service.ServicioAdicionalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.SERVICIOS)
@RequiredArgsConstructor
public class ServicioAdicionalController {

    private final ServicioAdicionalService servicioAdicionalService;

    @GetMapping
    public ResponseEntity<List<ServicioAdicionalResponse>> listar() {
        return ResponseEntity.ok(servicioAdicionalService.listarParaApi());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ServicioAdicionalResponse>> listarActivos() {
        return ResponseEntity.ok(servicioAdicionalService.listarActivosParaApi());
    }

    @GetMapping(ApiPaths.SERVICIOS_ID)
    public ResponseEntity<ServicioAdicionalResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioAdicionalService.obtenerParaApi(id));
    }
}
