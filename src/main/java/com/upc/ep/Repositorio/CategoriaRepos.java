package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepos extends JpaRepository<Categoria, Long> {
}
