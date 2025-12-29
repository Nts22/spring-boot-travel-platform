package com.ptirado.nmviajes.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
