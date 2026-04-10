package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Inventario;
import com.upc.ep.Entidades.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventarioRepos extends JpaRepository<Inventario, Long> {
    @Query("SELECT COALESCE(SUM(i.stock), 0) FROM Inventario i WHERE i.lote.idLote = :idLote")
    int sumStockByLote(@Param("idLote") Long idLote);

    @Query("""
       SELECT COALESCE(SUM(i.stock),0)
       FROM Inventario i
       WHERE i.lote.prenda.idPrenda = :idPrenda
       """)
    int sumStockByPrenda(@Param("idPrenda") Long idPrenda);

    List<Inventario> findByLote(Lote lote);
}
