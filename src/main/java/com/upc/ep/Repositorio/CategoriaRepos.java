package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepos extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByOrderByIdCategoriaDesc();
}
