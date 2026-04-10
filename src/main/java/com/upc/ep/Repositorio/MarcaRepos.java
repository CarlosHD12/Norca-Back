package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcaRepos extends JpaRepository<Marca, Long> {
    List<Marca> findAllByOrderByIdMarcaDesc();
}

