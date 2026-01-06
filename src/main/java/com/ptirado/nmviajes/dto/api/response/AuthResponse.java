package com.ptirado.nmviajes.dto.api.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private Set<String> roles;
    private String mensaje;
}
