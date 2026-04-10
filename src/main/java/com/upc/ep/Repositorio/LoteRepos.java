package com.upc.ep.Repositorio;

import com.upc.ep.DTO.LoteMensualDTO;
import com.upc.ep.Entidades.Lote;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoteRepos extends JpaRepository<Lote, Long> {
    List<Lote> findAllByOrderByIdLoteDesc();

    Lote findTopByPrendaIdPrendaAndActivoTrueOrderByFechaIngresoDescIdLoteDesc(Long idPrenda);

    @Query("""
    SELECT l FROM Lote l
    WHERE l.prenda.idPrenda = :idPrenda
    AND l.stockActual > 0
    ORDER BY l.fechaIngreso ASC
    """)
    List<Lote> obtenerLotesDisponiblesFIFO(Long idPrenda);

    @Modifying
    @Query("UPDATE Lote l SET l.activo = false WHERE l.prenda.idPrenda = :idPrenda AND l.activo = true")
    void desactivarLotesAnteriores(@Param("idPrenda") Long idPrenda);

    @Query("""
    SELECT COALESCE(SUM(l.stockActual),0)
    FROM Lote l
    WHERE l.activo = true
""")
    Integer totalStockDisponible();


    @Query("""
    SELECT COALESCE(SUM(l.stockActual),0)
    FROM Lote l
    WHERE l.activo = true
    AND l.fechaIngreso >= :fechaInicio
""")
    Integer stockUltimoMes(LocalDate fechaInicio);


    @Query("""
    SELECT COALESCE(SUM(l.stockActual),0)
    FROM Lote l
    WHERE l.activo = true
    AND l.fechaIngreso >= :fechaInicio
    AND l.fechaIngreso < :fechaFin
""")
    Integer stockMesAnterior(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("""
    SELECT COUNT(l)
    FROM Lote l
    WHERE l.activo = true
""")
    Long totalLotesActivos();

    @Query("""
    SELECT new com.upc.ep.DTO.LoteMensualDTO(
        YEAR(l.fechaIngreso),
        MONTH(l.fechaIngreso),
        COUNT(l)
    )
    FROM Lote l
    GROUP BY YEAR(l.fechaIngreso), MONTH(l.fechaIngreso)
    ORDER BY YEAR(l.fechaIngreso), MONTH(l.fechaIngreso)
""")
    List<LoteMensualDTO> obtenerLotesPorMes();

    @Query("""
    SELECT DISTINCT l
    FROM Lote l
    LEFT JOIN FETCH l.inventarios i
    LEFT JOIN FETCH i.talla
    WHERE l.prenda.idPrenda = :idPrenda
    ORDER BY l.fechaIngreso DESC, l.idLote DESC
""")
    List<Lote> obtenerHistorialPorPrenda(@Param("idPrenda") Long idPrenda);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(l.numeroLote) FROM Lote l WHERE l.prenda.idPrenda = :prendaId")
    Integer findMaxNumeroLoteByPrendaIdForUpdate(@Param("prendaId") Long prendaId);
}
