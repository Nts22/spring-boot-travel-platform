package com.ptirado.nmviajes.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.dto.api.request.RegistroRequest;
import com.ptirado.nmviajes.dto.api.response.AuthResponse;
import com.ptirado.nmviajes.entity.Role;
import com.ptirado.nmviajes.entity.Usuario;
import com.ptirado.nmviajes.exception.api.ConflictException;
import com.ptirado.nmviajes.repository.RoleRepository;
import com.ptirado.nmviajes.repository.UsuarioRepository;
import com.ptirado.nmviajes.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ESTADO_ACTIVO = "ACT";

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        Role roleUser = roleRepository.findByNombre(ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setEstado(ESTADO_ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.getRoles().add(roleUser);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Set<String> roles = usuarioGuardado.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .idUsuario(usuarioGuardado.getIdUsuario())
                .nombre(usuarioGuardado.getNombre())
                .apellido(usuarioGuardado.getApellido())
                .email(usuarioGuardado.getEmail())
                .roles(roles)
                .mensaje("Usuario registrado exitosamente")
                .build();
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
