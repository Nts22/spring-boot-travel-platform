package com.ptirado.nmviajes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptirado.nmviajes.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
