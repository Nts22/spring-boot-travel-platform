package com.ptirado.nmviajes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.ReservaItemServicio;
import com.ptirado.nmviajes.entity.ReservaItemServicioId;

@Repository
public interface ReservaItemServicioRepository extends JpaRepository<ReservaItemServicio, ReservaItemServicioId> {
}
