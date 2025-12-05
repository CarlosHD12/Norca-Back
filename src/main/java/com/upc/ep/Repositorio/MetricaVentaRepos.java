package com.upc.ep.Repositorio;

import com.upc.ep.DTO.PrendaOlvidadaDTO;
import com.upc.ep.Entidades.MetricaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MetricaVentaRepos extends JpaRepository<MetricaVenta, Long> {
    List<MetricaVenta> findByPrendaId(Long prendaId);

    @Query("""
    SELECT mv.prendaId, SUM(mv.unidadesVendidas) AS totalVendidas
    FROM MetricaVenta mv
    GROUP BY mv.prendaId
    ORDER BY totalVendidas DESC
    """)
    List<Object[]> top10PrendasVendidasRaw();

    @Query("""
    SELECT new com.upc.ep.DTO.PrendaOlvidadaDTO(
        c.nombre,
        m.marca,
        p.calidad,
        p.stock,
        p.precioVenta
    )
    FROM Prenda p
    LEFT JOIN MetricaVenta mv ON mv.prendaId = p.idPrenda
    LEFT JOIN p.marca m
    LEFT JOIN m.categoria c
    WHERE mv.prendaId IS NULL
      AND FUNCTION('AGE', FUNCTION('NOW'), p.fechaRegistro) > FUNCTION('MAKE_INTERVAL', 0, 0, 0, 15)
    ORDER BY p.stock ASC
    """)
    List<PrendaOlvidadaDTO> listarPrendasOlvidadas();



}
