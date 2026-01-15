package com.ptirado.nmviajes.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Filtros para admin
    Page<Usuario> findByEstado(String estado, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Usuario> findByBusqueda(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.estado = :estado AND " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Usuario> findByEstadoAndBusqueda(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);
}
