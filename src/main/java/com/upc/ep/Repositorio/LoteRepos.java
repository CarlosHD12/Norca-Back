package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoteRepos extends JpaRepository<Lote, Long> {
    @Query("SELECT l FROM Lote l WHERE l.prenda.idPrenda = :prendaId ORDER BY l.idLote DESC")
    List<Lote> findByPrendaIdOrderByFechaIngresoDesc(@Param("prendaId") Long prendaId);
}
