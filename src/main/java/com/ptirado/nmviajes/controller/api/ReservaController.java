package com.ptirado.nmviajes.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.security.CustomUserDetails;
import com.ptirado.nmviajes.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.RESERVAS)
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listarParaApi());
    }

    @GetMapping(ApiPaths.RESERVAS_ID)
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(reservaService.obtenerParaApi(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.crearDesdeApi(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaResponse>> listarPorUsuario(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idUsuario) {
        // Un usuario solo puede ver sus propias reservas, a menos que sea ADMIN
        if (!userDetails.getIdUsuario().equals(idUsuario) &&
            !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reservaService.listarPorUsuarioParaApi(idUsuario));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponse>> listarMisReservas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reservaService.listarPorUsuarioParaApi(userDetails.getIdUsuario()));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<ReservaResponse> confirmarPago(@PathVariable Integer id) {
        return ResponseEntity.ok(reservaService.confirmarPago(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }
}
