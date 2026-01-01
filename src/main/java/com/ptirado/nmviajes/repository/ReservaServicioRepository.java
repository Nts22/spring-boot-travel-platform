package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.ReservaServicio;
import com.ptirado.nmviajes.entity.ReservaServicioId;

@Repository
public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, ReservaServicioId> {

    List<ReservaServicio> findByReserva_IdReserva(Integer idReserva);

    void deleteByReserva_IdReserva(Integer idReserva);
}
