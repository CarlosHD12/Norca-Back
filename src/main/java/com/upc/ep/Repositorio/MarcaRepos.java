package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarcaRepos extends JpaRepository<Marca, Long> {
    boolean existsByNombre(String nombre);
    Optional<Marca> findByIdMarcaAndActivoTrue(Long idMarca);
    List<Marca> findAllByOrderByIdMarcaDesc();
    boolean existsByNombreIgnoreCase(String nombre);


}

