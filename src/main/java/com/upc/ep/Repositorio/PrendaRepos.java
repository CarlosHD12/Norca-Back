package com.upc.ep.Repositorio;

import com.upc.ep.DTO.PrendaListadoDTO;
import com.upc.ep.DTO.PrendaOlvidadaDTO;
import com.upc.ep.DTO.StockBajoDTO;
import com.upc.ep.DTO.StockCategoriaDTO;
import com.upc.ep.Entidades.Inventario;
import com.upc.ep.Entidades.Prenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrendaRepos extends JpaRepository<Prenda, Long> {
    Optional<Prenda> findById(Long idPrenda);

    @Query("SELECT p FROM Prenda p ORDER BY p.idPrenda DESC")
    List<Prenda> findAllPrendasOrdenDesc();

    @Query("""
    SELECT DISTINCT p
    FROM Prenda p
    LEFT JOIN FETCH p.categoria
    LEFT JOIN FETCH p.marca
    LEFT JOIN FETCH p.lotes l
    LEFT JOIN FETCH l.inventarios i
    LEFT JOIN FETCH i.talla
    WHERE p.idPrenda = :id
    """)
    Optional<Prenda> obtenerDetallePrenda(Long id);

    @Query("SELECT p FROM Prenda p WHERE p.estado = :estado ORDER BY p.idPrenda DESC")
    List<Prenda> findByEstadoOrderByIdDesc(@Param("estado") String estado);

    @Query("SELECT p.categoria.nombre, COUNT(p) FROM Prenda p GROUP BY p.categoria.nombre")
    List<Object[]> countByCategoria();

    @Query("SELECT p.marca.nombre, COUNT(p) FROM Prenda p GROUP BY p.marca.nombre")
    List<Object[]> countByMarca();

    @Query("SELECT p.estado, COUNT(p) FROM Prenda p GROUP BY p.estado")
    List<Object[]> countByEstado();

    @Query("""
    SELECT new com.upc.ep.DTO.PrendaOlvidadaDTO(
        p.idPrenda,
        p.categoria.nombre,
        p.marca.nombre,
        p.material,
        p.descripcion,
        l.stockActual,
        l.fechaIngreso,
        (
            SELECT MAX(v2.fechaHora)
            FROM Lote l2
            JOIN l2.inventarios i2
            JOIN i2.detalle_Vents dv2
            JOIN dv2.venta v2
            WHERE l2.prenda.idPrenda = p.idPrenda
        )
    )
    FROM Prenda p
    JOIN p.lotes l
    WHERE l.activo = true
    AND l.stockActual > 0
    AND (
        (
            SELECT MAX(v2.fechaHora)
            FROM Lote l2
            JOIN l2.inventarios i2
            JOIN i2.detalle_Vents dv2
            JOIN dv2.venta v2
            WHERE l2.prenda.idPrenda = p.idPrenda
        ) IS NULL
        OR
        (
            SELECT MAX(v2.fechaHora)
            FROM Lote l2
            JOIN l2.inventarios i2
            JOIN i2.detalle_Vents dv2
            JOIN dv2.venta v2
            WHERE l2.prenda.idPrenda = p.idPrenda
        ) <= :fechaLimite
    )
    """)
    List<PrendaOlvidadaDTO> findPrendasOlvidadas(LocalDateTime fechaLimite);

    @Query("""
    SELECT 
        p.idPrenda,
        p.categoria.nombre,
        p.marca.nombre,
        p.material,
        p.descripcion,
        SUM(dv.cantidad)
    FROM Prenda p
    JOIN p.lotes l
    JOIN l.inventarios i
    JOIN i.detalle_Vents dv
    GROUP BY p.idPrenda, p.categoria.nombre, p.marca.nombre, p.material, p.descripcion
    ORDER BY SUM(dv.cantidad) DESC
    """)
    List<Object[]> rankingPrendasMasVendidas();

    @Query("""
    SELECT new com.upc.ep.DTO.StockBajoDTO(
        p.idPrenda,
        c.nombre,
        m.nombre,
        p.material,
        p.descripcion,
        l.idLote,
        l.cantidad,
        SUM(i.stock)
    )
    FROM Prenda p
    JOIN p.categoria c
    JOIN p.marca m
    JOIN p.lotes l
    JOIN l.inventarios i
    WHERE l.activo = true
    GROUP BY p.idPrenda, c.nombre, m.nombre, p.material, p.descripcion, l.idLote, l.cantidad
    HAVING SUM(i.stock) <= :limite
""")
    List<StockBajoDTO> bajoStock(@Param("limite") Integer limite);

    @Query("SELECT COUNT(p) FROM Prenda p")
    Long totalPrendas();

    @Query("""
    SELECT COUNT(p)
    FROM Prenda p
    WHERE p.fechaRegistro >= :inicio
    """)
    Long prendasDesde(@Param("inicio") LocalDate inicio);

    @Query("""
    SELECT COUNT(p)
    FROM Prenda p
    WHERE p.fechaRegistro BETWEEN :inicio AND :fin
    """)
    Long prendasEntre(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("""
    SELECT COUNT(p)
    FROM Prenda p
    WHERE p.estado = 'AGOTADO'
""")
    Long totalPrendasAgotadas();

    @Query("""
    SELECT new com.upc.ep.DTO.StockCategoriaDTO(
        c.nombre,
        SUM(l.stockActual)
    )
    FROM Lote l
    JOIN l.prenda p
    JOIN p.categoria c
    WHERE l.activo = true
    GROUP BY c.nombre
    """)
    List<StockCategoriaDTO> stockPorCategoria();

    @Query("SELECT i FROM Inventario i " +
            "JOIN i.lote l " +
            "JOIN l.prenda p " +
            "WHERE p.idPrenda = ?1 " +
            "AND l.activo = true " +
            "AND i.stock > 0 " +
            "ORDER BY l.fechaIngreso ASC, l.idLote ASC")
    List<Inventario> findInventarioActivoFIFO(Long idPrenda);
}