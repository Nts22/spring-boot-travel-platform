package com.ptirado.nmviajes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuario_IdUsuario(Integer idUsuario);

    Page<Reserva> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);

    List<Reserva> findByEstadoReserva(EstadoReserva estadoReserva);

    Page<Reserva> findByEstadoReserva(EstadoReserva estadoReserva, Pageable pageable);

    List<Reserva> findByUsuario_IdUsuarioAndEstadoReserva(Integer idUsuario, EstadoReserva estadoReserva);

    @Query("SELECT DISTINCT r FROM Reserva r JOIN r.items i WHERE i.paquete.idPaquete = :idPaquete")
    List<Reserva> findByPaqueteId(@Param("idPaquete") Integer idPaquete);

    @Query("SELECT COUNT(DISTINCT r) FROM Reserva r JOIN r.items i WHERE i.paquete.idPaquete = :idPaquete AND r.estadoReserva = :estadoReserva")
    Long countByPaqueteAndEstadoReserva(@Param("idPaquete") Integer idPaquete, @Param("estadoReserva") EstadoReserva estadoReserva);

    Long countByEstadoReserva(EstadoReserva estadoReserva);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario ORDER BY r.fechaCreacion DESC LIMIT 5")
    List<Reserva> findTop5ByOrderByFechaCreacionDesc();

    // Admin panel queries with eager loading
    @Query(value = "SELECT r FROM Reserva r JOIN FETCH r.usuario LEFT JOIN FETCH r.items",
           countQuery = "SELECT COUNT(r) FROM Reserva r")
    Page<Reserva> findAllWithUsuarioAndItems(Pageable pageable);

    @Query(value = "SELECT r FROM Reserva r JOIN FETCH r.usuario LEFT JOIN FETCH r.items WHERE r.estadoReserva = :estadoReserva",
           countQuery = "SELECT COUNT(r) FROM Reserva r WHERE r.estadoReserva = :estadoReserva")
    Page<Reserva> findByEstadoReservaWithUsuarioAndItems(@Param("estadoReserva") EstadoReserva estadoReserva, Pageable pageable);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario LEFT JOIN FETCH r.items WHERE r.idReserva = :id")
    Optional<Reserva> findByIdWithUsuarioAndItems(@Param("id") Integer id);
}
