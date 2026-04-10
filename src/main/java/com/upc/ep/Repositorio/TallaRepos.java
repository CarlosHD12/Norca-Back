package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Talla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallaRepos extends JpaRepository<Talla, Long> {
    List<Talla> findAllByOrderByIdTallaDesc();
}