package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepos extends JpaRepository<Categoria, Long> {
    boolean existsByNombre(String nombre);
    Optional<Categoria> findByIdCategoriaAndActivoTrue(Long idCategoria);
    List<Categoria> findAllByOrderByIdCategoriaDesc();
    boolean existsByNombreIgnoreCase(String nombre);
}
