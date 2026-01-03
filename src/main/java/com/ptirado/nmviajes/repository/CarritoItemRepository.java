package com.ptirado.nmviajes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.CarritoItem;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {

    List<CarritoItem> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<CarritoItem> findByCarrito_IdCarritoAndPaquete_IdPaquete(Integer idCarrito, Integer idPaquete);

    void deleteByCarrito_IdCarrito(Integer idCarrito);
}
