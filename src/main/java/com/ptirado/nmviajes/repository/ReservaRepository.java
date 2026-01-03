package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuario_IdUsuario(Integer idUsuario);

    List<Reserva> findByEstadoReserva(EstadoReserva estadoReserva);

    List<Reserva> findByUsuario_IdUsuarioAndEstadoReserva(Integer idUsuario, EstadoReserva estadoReserva);

    @Query("SELECT DISTINCT r FROM Reserva r JOIN r.items i WHERE i.paquete.idPaquete = :idPaquete")
    List<Reserva> findByPaqueteId(@Param("idPaquete") Integer idPaquete);

    @Query("SELECT COUNT(DISTINCT r) FROM Reserva r JOIN r.items i WHERE i.paquete.idPaquete = :idPaquete AND r.estadoReserva = :estadoReserva")
    Long countByPaqueteAndEstadoReserva(@Param("idPaquete") Integer idPaquete, @Param("estadoReserva") EstadoReserva estadoReserva);
}
