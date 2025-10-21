package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrendaRepos extends JpaRepository<Prenda, Long> {
}
