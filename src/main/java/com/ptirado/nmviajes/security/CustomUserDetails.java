package com.ptirado.nmviajes.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ptirado.nmviajes.entity.Usuario;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACT".equals(usuario.getEstado());
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Integer getIdUsuario() {
        return usuario.getIdUsuario();
    }

    public String getNombreCompleto() {
        return usuario.getNombre() + " " + usuario.getApellido();
    }

    /**
     * Devuelve el nombre del usuario para mostrar en la UI.
     * Este metodo es usado por sec:authentication="name"
     * Muestra nombre + apellido si existe, sino solo el nombre.
     */
    public String getName() {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();
        if (apellido != null && !apellido.trim().isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }
}
