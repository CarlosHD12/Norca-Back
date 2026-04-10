package com.upc.ep.Repositorio;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepos extends JpaRepository<Venta, Long> {
    List<Venta> findAllByOrderByIdVentaDesc();

    @Query("""
SELECT new com.upc.ep.DTO.VentaListadoDTO(
    v.idVenta,
    v.codigo,
    v.unidades,
    v.fechaHora,
    v.metodoPago,
    v.total,
    v.estado
)
FROM Venta v
WHERE
v.estado = true

AND LOWER(v.codigo) LIKE LOWER(CONCAT('%', :codigo, '%'))
AND v.metodoPago LIKE %:metodoPago%

AND (
    :periodo = '' OR
    (:periodo = 'MANANA' AND EXTRACT(HOUR FROM v.fechaHora) BETWEEN 6 AND 11) OR
    (:periodo = 'TARDE' AND EXTRACT(HOUR FROM v.fechaHora) BETWEEN 12 AND 17) OR
    (:periodo = 'NOCHE' AND EXTRACT(HOUR FROM v.fechaHora) BETWEEN 18 AND 23)
)

AND v.fechaHora >= :fechaInicio AND v.fechaHora < :fechaFin
AND v.total BETWEEN :precioMin AND :precioMax
AND v.unidades BETWEEN :unidadesMin AND :unidadesMax

ORDER BY v.idVenta DESC
""")
    Page<VentaListadoDTO> listarVentas(
            @Param("codigo") String codigo,
            @Param("metodoPago") String metodoPago,
            @Param("periodo") String periodo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("unidadesMin") Integer unidadesMin,
            @Param("unidadesMax") Integer unidadesMax,
            Pageable pageable
    );

    @Query("SELECT v.fechaHora FROM Venta v JOIN v.detalle_Vents dv WHERE dv.inventario.lote.prenda.idPrenda = :prendaId")
    List<Timestamp> findAllFechasByPrenda(@Param("prendaId") Long prendaId);

    @Query("SELECT v FROM Venta v " +
            "LEFT JOIN FETCH v.detalle_Vents d " +
            "LEFT JOIN FETCH d.inventario i " +
            "LEFT JOIN FETCH i.lote l " +
            "LEFT JOIN FETCH l.prenda p " +
            "LEFT JOIN FETCH p.categoria c " +
            "LEFT JOIN FETCH p.marca m " +
            "LEFT JOIN FETCH i.talla t " +
            "WHERE v.idVenta = :id")
    Optional<Venta> obtenerDetalleVenta(@Param("id") Long id);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = true")
    Long totalVentas();

    @Query("""
        SELECT COUNT(v)
        FROM Venta v
        WHERE v.estado = true
        AND v.fechaHora >= :fechaInicio
    """)
    Long ventasUltimoMes(LocalDateTime fechaInicio);


    @Query("""
        SELECT COALESCE(SUM(v.unidades),0)
        FROM Venta v
        WHERE v.estado = true
    """)
    Long unidadesTotales();


    @Query("""
        SELECT COALESCE(SUM(v.unidades),0)
        FROM Venta v
        WHERE v.estado = true
        AND v.fechaHora >= :fechaInicio
    """)
    Long unidadesUltimoMes(LocalDateTime fechaInicio);


    @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE v.estado = true
    """)
    Double ingresosTotales();


    @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE v.estado = true
        AND v.fechaHora >= :fechaInicio
    """)
    Double ingresosUltimoMes(LocalDateTime fechaInicio);


    @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE v.estado = true
        AND v.fechaHora >= :fechaInicio
        AND v.fechaHora < :fechaFin
    """)
    Double ingresosMesAnterior(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("""
    SELECT new com.upc.ep.DTO.MetodoPagoDTO(
        v.metodoPago,
        COUNT(v)
    )
    FROM Venta v
    WHERE v.estado = true
    GROUP BY v.metodoPago
    ORDER BY COUNT(v) DESC
    """)
    List<MetodoPagoDTO> metodoPagoMasUsado(Pageable pageable);

    @Query("""
    SELECT new com.upc.ep.DTO.IngresosCategoriaDTO(
        c.nombre,
        SUM(dv.subtotal),
        SUM(dv.cantidad)
    )
    FROM Detalle_Vent dv
    JOIN dv.inventario i
    JOIN i.lote l
    JOIN l.prenda p
    JOIN p.categoria c
    JOIN dv.venta v
    WHERE v.estado = true
    GROUP BY c.nombre
    ORDER BY SUM(dv.subtotal) DESC
""")
    List<IngresosCategoriaDTO> obtenerIngresosPorCategoria();

    @Query("""
        SELECT new com.upc.ep.DTO.VentasMesDTO(
            EXTRACT(MONTH FROM v.fechaHora),
            EXTRACT(YEAR FROM v.fechaHora),
            COUNT(v.idVenta),
            SUM(v.total)
        )
        FROM Venta v
        GROUP BY EXTRACT(YEAR FROM v.fechaHora), EXTRACT(MONTH FROM v.fechaHora)
        ORDER BY EXTRACT(YEAR FROM v.fechaHora), EXTRACT(MONTH FROM v.fechaHora)
    """)
    List<VentasMesDTO> obtenerVentasOMes();

    @Query("SELECT v FROM Venta v " +
            "JOIN FETCH v.detalle_Vents dv " +
            "JOIN FETCH dv.inventario i " +
            "JOIN FETCH i.lote l " +
            "JOIN FETCH l.prenda p " +
            "JOIN FETCH p.categoria c " +
            "JOIN FETCH p.marca m " +
            "WHERE v.idVenta = :id")
    Optional<Venta> findVentaWithAll(@Param("id") Long id);
}
