package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.ServicioAdicional;

@Repository
public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Integer> {

    List<ServicioAdicional> findByEstado(String estado);

    boolean existsByNombre(String nombre);
}
