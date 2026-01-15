package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.ServicioAdicional;

@Repository
public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Integer> {

    List<ServicioAdicional> findByEstado(String estado);

    boolean existsByNombre(String nombre);

    // Filtros para admin
    Page<ServicioAdicional> findByEstado(String estado, Pageable pageable);

    @Query("SELECT s FROM ServicioAdicional s WHERE LOWER(s.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<ServicioAdicional> findByBusqueda(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("SELECT s FROM ServicioAdicional s WHERE s.estado = :estado AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<ServicioAdicional> findByEstadoAndBusqueda(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);
}
