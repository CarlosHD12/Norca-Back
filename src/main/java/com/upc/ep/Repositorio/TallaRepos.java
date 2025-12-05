package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TallaRepos extends JpaRepository<Talla, Long> {
    // Obtener todas las tallas por ID de prenda
    @Query("SELECT t FROM Talla t WHERE t.prenda.idPrenda = :idPrenda")
    List<Talla> findByPrendaId(@Param("idPrenda") Long idPrenda);

    // Sumar el stock total de una prenda
    @Query("SELECT SUM(t.cantidad) FROM Talla t WHERE t.prenda.idPrenda = :idPrenda")
    Integer sumStockByPrendaId(@Param("idPrenda") Long idPrenda);


    Optional<Talla> findByPrendaIdPrendaAndSize(Long prendaId, String size);

    //Sumar todas las tallas
    @Query("SELECT SUM(t.cantidad) FROM Talla t WHERE t.prenda.idPrenda = :idPrenda AND t.idTalla <> :idTalla")
    Integer sumStockByPrendaIdExcluyendoTalla(@Param("idPrenda") Long idPrenda, @Param("idTalla") Long idTalla);

    // En TallaRepos.java
    @Query("SELECT t FROM Talla t WHERE t.idTalla IN :ids AND t.idTalla IN (SELECT DISTINCT dv.talla.idTalla FROM Detalle_Vent dv)")
    List<Talla> findTallasVinculadasEnVentas(@Param("ids") List<Long> ids);

    @Query("SELECT t FROM Talla t WHERE t.prenda = :prenda AND LOWER(TRIM(t.size)) = LOWER(TRIM(:size))")
    Optional<Talla> findByPrendaAndSize(@Param("prenda") Prenda prenda, @Param("size") String size);
}