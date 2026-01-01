package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuario_IdUsuario(Integer idUsuario);

    List<Reserva> findByPaquete_IdPaquete(Integer idPaquete);

    List<Reserva> findByEstadoReserva(String estadoReserva);

    List<Reserva> findByUsuario_IdUsuarioAndEstado(Integer idUsuario, String estado);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.paquete.idPaquete = :idPaquete AND r.estado = :estado")
    Long countByPaqueteAndEstado(@Param("idPaquete") Integer idPaquete, @Param("estado") String estado);
}
