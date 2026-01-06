package com.ptirado.nmviajes.controller.api;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.request.RegistroRequest;
import com.ptirado.nmviajes.dto.api.response.AuthResponse;
import com.ptirado.nmviajes.entity.Role;
import com.ptirado.nmviajes.security.CustomUserDetails;
import com.ptirado.nmviajes.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPaths.AUTH)
public class AuthApiController {

    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(ApiPaths.AUTH_REGISTRO)
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(ApiPaths.AUTH_PERFIL)
    public ResponseEntity<AuthResponse> obtenerPerfil(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthResponse response = AuthResponse.builder()
                .idUsuario(userDetails.getIdUsuario())
                .nombre(userDetails.getUsuario().getNombre())
                .apellido(userDetails.getUsuario().getApellido())
                .email(userDetails.getUsername())
                .roles(userDetails.getUsuario().getRoles().stream()
                        .map(Role::getNombre)
                        .collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok(response);
    }
}
