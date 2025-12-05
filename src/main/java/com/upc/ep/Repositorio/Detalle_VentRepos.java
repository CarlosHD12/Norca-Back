package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Detalle_Vent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Total de unidades vendidas de una prenda
    @Query("SELECT COALESCE(SUM(dv.cantidad), 0) FROM Detalle_Vent dv WHERE dv.prenda.idPrenda = :idPrenda")
    Integer totalUnidadesVendidas(@Param("idPrenda") Long idPrenda);

    // Total de ingresos generados por una prenda
    @Query("SELECT COALESCE(SUM(dv.subTotal), 0) FROM Detalle_Vent dv WHERE dv.prenda.idPrenda = :idPrenda")
    Double ingresosTotales(@Param("idPrenda") Long idPrenda);

    // Cantidad total de ventas realizadas
    @Query("SELECT COALESCE(COUNT(dv), 0) FROM Detalle_Vent dv WHERE dv.prenda.idPrenda = :idPrenda")
    Integer cantidadVentas(@Param("idPrenda") Long idPrenda);


    // Fecha de última venta
    @Query("SELECT MAX(dv.venta.fechahoraVenta) FROM Detalle_Vent dv WHERE dv.prenda.idPrenda = :idPrenda")
    LocalDateTime ultimaVenta(Long idPrenda);

    // Ranking — Top N prendas más vendidas
    @Query("""
        SELECT dv.prenda.idPrenda, SUM(dv.cantidad) as total
        FROM Detalle_Vent dv
        GROUP BY dv.prenda.idPrenda
        ORDER BY total DESC
    """)
    List<Object[]> rankingPrendas();

    // Fechas para calcular tiempo promedio de venta
    @Query("""
        SELECT dv.venta.fechahoraVenta 
        FROM Detalle_Vent dv
        WHERE dv.prenda.idPrenda = :idPrenda
        ORDER BY dv.venta.fechahoraVenta ASC
    """)
    List<LocalDateTime> fechasDeVenta(Long idPrenda);
}