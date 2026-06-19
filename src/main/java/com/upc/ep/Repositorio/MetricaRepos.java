package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Metrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricaRepos extends JpaRepository<Metrica, Long> {
    @Query("""
    SELECT m
    FROM Metrica m
    JOIN FETCH m.prenda p
    WHERE p.idPrenda = :idPrenda
    """)
    Optional<Metrica> obtenerPorIdPrenda(@Param("idPrenda") Long idPrenda);}
