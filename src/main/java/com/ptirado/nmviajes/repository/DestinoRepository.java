package com.ptirado.nmviajes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Destino;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Integer> {
    Optional<Destino> findByNombre(String nombre);

    java.util.List<Destino> findByEstado(String estado);
}
