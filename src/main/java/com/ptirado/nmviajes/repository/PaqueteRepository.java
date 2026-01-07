package com.ptirado.nmviajes.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Destino;
import com.ptirado.nmviajes.entity.Paquete;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {

    List<Paquete> findByDestino(Destino destino);

    List<Paquete> findByDestino_IdDestino(Integer idDestino);

    List<Paquete> findByEstado(String estado);

    List<Paquete> findByDestinoAndEstado(Destino destino, String estado);

    List<Paquete> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    List<Paquete> findByFechaInicioGreaterThanEqual(LocalDate fecha);

    List<Paquete> findByNombreContainingIgnoreCase(String nombre);

    List<Paquete> findByStockDisponibleGreaterThan(Integer stock);

    @Query("""
        SELECT p FROM Paquete p
        WHERE (:idDestino IS NULL OR p.destino.idDestino = :idDestino)
          AND (:fechaInicio IS NULL OR p.fechaInicio >= :fechaInicio)
          AND (:fechaFin IS NULL OR p.fechaFin <= :fechaFin)
        ORDER BY p.fechaInicio ASC
        """)
    List<Paquete> buscar(
        @Param("idDestino") Integer idDestino,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    // Admin panel queries with eager loading
    @Query(value = "SELECT p FROM Paquete p LEFT JOIN FETCH p.destino",
           countQuery = "SELECT COUNT(p) FROM Paquete p")
    Page<Paquete> findAllWithDestino(Pageable pageable);

    @Query("SELECT p FROM Paquete p LEFT JOIN FETCH p.destino WHERE p.idPaquete = :id")
    Optional<Paquete> findByIdWithDestino(@Param("id") Integer id);
}
