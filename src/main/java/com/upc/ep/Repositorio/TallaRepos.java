package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Talla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TallaRepos extends JpaRepository<Talla, Long> {
    boolean existsByNombre(String nombre);
    Optional<Talla> findByIdTallaAndActivoTrue(Long idTalla);
    List<Talla> findAllByOrderByIdTallaDesc();
    boolean existsByNombreIgnoreCase(String nombre);
}