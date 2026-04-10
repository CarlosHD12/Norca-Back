package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Metrica;
import com.upc.ep.Entidades.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricaRepos extends JpaRepository<Metrica, Long> {
    Optional<Metrica> findByPrenda(Prenda prenda);

    Optional<Metrica> findByPrenda_IdPrenda(Long idPrenda);

    @Query("""
   SELECT SUM(dv.cantidad), SUM(dv.subtotal)
   FROM Detalle_Vent dv
   WHERE dv.inventario.lote.prenda.idPrenda = :idPrenda
   AND dv.venta.idVenta = :idVenta
""")
    Object sumVentasByPrendaYVenta(Long idPrenda, Long idVenta);

    @Query("SELECT SUM(l.precioCompraTotal) FROM Lote l WHERE l.prenda.idPrenda = :idPrenda")
    Double sumPrecioCompraTotalByPrenda(Long idPrenda);

    @Query("SELECT dv.venta.fechaHora FROM Detalle_Vent dv " +
            "WHERE dv.inventario.lote.prenda.idPrenda = :idPrenda " +
            "ORDER BY dv.venta.fechaHora ASC")
    List<LocalDateTime> findAllFechasByPrenda(Long idPrenda);

    boolean existsByPrenda_IdPrenda(Long idPrenda);
}
