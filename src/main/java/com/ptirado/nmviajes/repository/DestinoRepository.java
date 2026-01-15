package com.ptirado.nmviajes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Destino;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Integer> {
    Optional<Destino> findByNombre(String nombre);

    List<Destino> findByEstado(String estado);

    // Admin panel query with eager loading of paquetes count
    @Query(value = "SELECT d FROM Destino d LEFT JOIN FETCH d.paquetes",
           countQuery = "SELECT COUNT(d) FROM Destino d")
    Page<Destino> findAllWithPaquetes(Pageable pageable);

    // Filtros para admin
    @Query(value = "SELECT d FROM Destino d LEFT JOIN FETCH d.paquetes WHERE d.estado = :estado",
           countQuery = "SELECT COUNT(d) FROM Destino d WHERE d.estado = :estado")
    Page<Destino> findByEstadoWithPaquetes(@Param("estado") String estado, Pageable pageable);

    @Query(value = "SELECT d FROM Destino d LEFT JOIN FETCH d.paquetes WHERE " +
           "LOWER(d.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(d.pais) LIKE LOWER(CONCAT('%', :busqueda, '%'))",
           countQuery = "SELECT COUNT(d) FROM Destino d WHERE " +
           "LOWER(d.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(d.pais) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Destino> findByBusquedaWithPaquetes(@Param("busqueda") String busqueda, Pageable pageable);

    @Query(value = "SELECT d FROM Destino d LEFT JOIN FETCH d.paquetes WHERE d.estado = :estado AND " +
           "(LOWER(d.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(d.pais) LIKE LOWER(CONCAT('%', :busqueda, '%')))",
           countQuery = "SELECT COUNT(d) FROM Destino d WHERE d.estado = :estado AND " +
           "(LOWER(d.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(d.pais) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Destino> findByEstadoAndBusquedaWithPaquetes(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);
}
