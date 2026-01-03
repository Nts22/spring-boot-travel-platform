package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.CarritoItemServicio;
import com.ptirado.nmviajes.entity.CarritoItemServicioId;

@Repository
public interface CarritoItemServicioRepository extends JpaRepository<CarritoItemServicio, CarritoItemServicioId> {

    List<CarritoItemServicio> findByCarritoItem_IdItem(Integer idItem);

    void deleteByCarritoItem_IdItem(Integer idItem);
}
