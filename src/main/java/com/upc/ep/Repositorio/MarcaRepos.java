package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarcaRepos extends JpaRepository<Marca, Long> {
    //Lista Marcas sin repetir
    @Query("SELECT DISTINCT m FROM Marca m")
    List<Marca> findAllDistinct();

    //Lista Marcas por Categoria
    @Query("SELECT m FROM Marca m WHERE m.categoria.idCategoria = :idCategoria")
    List<Marca> findMarcasByCategoria(@Param("idCategoria") Long idCategoria);
}

