package com.ptirado.nmviajes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuario_IdUsuario(Integer idUsuario);

    @Query("SELECT COUNT(ci) FROM CarritoItem ci WHERE ci.carrito.usuario.idUsuario = :idUsuario")
    Integer contarItemsPorUsuario(@Param("idUsuario") Integer idUsuario);
}
