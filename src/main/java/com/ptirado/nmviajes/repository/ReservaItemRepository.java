package com.ptirado.nmviajes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.ReservaItem;

@Repository
public interface ReservaItemRepository extends JpaRepository<ReservaItem, Integer> {

    List<ReservaItem> findByReserva_IdReserva(Integer idReserva);
}
