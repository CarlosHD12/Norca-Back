package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Detalle_Vent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Detalle_VentRepos extends JpaRepository<Detalle_Vent, Long> {
    // Buscar detalles por ID de prenda
    @Query("SELECT d FROM Detalle_Vent d WHERE d.prenda.idPrenda = :idPrenda")
    List<Detalle_Vent> findByPrendaId(@Param("idPrenda") Long idPrenda);

    // Verificar si una prenda fue usada en alguna venta
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Detalle_Vent d WHERE d.prenda.idPrenda = :idPrenda")
    boolean existePorPrenda(@Param("idPrenda") Long idPrenda);

    boolean existsByPrenda_IdPrenda(Long idPrenda);
    void deleteByVentaIdVenta(Long idVenta);
    Integer countByVentaIdVenta(Long idVenta);
    List<Detalle_Vent> findByVentaIdVenta(Long idVenta);
    @Query("SELECT COALESCE(SUM(d.subTotal), 0) FROM Detalle_Vent d WHERE d.venta.idVenta = :idVenta")
    Double sumTotalByVenta(@Param("idVenta") Long idVenta);
}
