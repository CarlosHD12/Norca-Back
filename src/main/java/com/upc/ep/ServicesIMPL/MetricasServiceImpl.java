package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.PrendaMetricasDTO;
import com.upc.ep.DTO.PrendaOlvidadaDTO;
import com.upc.ep.DTO.PrendaStockBajoDTO;
import com.upc.ep.DTO.TopPrendaDTO;
import com.upc.ep.Entidades.MetricaVenta;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.Detalle_VentRepos;
import com.upc.ep.Repositorio.MetricaVentaRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Services.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MetricasServiceImpl implements MetricasService {
    @Autowired
    private Detalle_VentRepos detalleRepos;

    @Autowired
    private MetricaVentaRepos metricaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Override
    public PrendaMetricasDTO obtenerMetricas(Long idPrenda) {
        // Solo pasa idPrenda
        List<MetricaVenta> metricas = metricaRepos.findByPrendaId(idPrenda);

        int totalUnidadesVendidas = metricas.stream()
                .mapToInt(MetricaVenta::getUnidadesVendidas)
                .sum();

        double ingresos = metricas.stream()
                .mapToDouble(MetricaVenta::getIngresos)
                .sum();

        double gananciaAcumulada = metricas.stream()
                .mapToDouble(MetricaVenta::getGanancia)
                .sum();

        LocalDateTime ultimaVenta = null;
        for (MetricaVenta m : metricas) {
            if (ultimaVenta == null || m.getFechaVenta().isAfter(ultimaVenta)) {
                ultimaVenta = m.getFechaVenta();
            }
        }

        // Contar ventas únicas
        Set<Long> ventasUnicas = metricas.stream()
                .map(MetricaVenta::getVentaId)
                .collect(Collectors.toSet());
        int cantVentas = ventasUnicas.size();

        List<Object[]> ranking = detalleRepos.rankingPrendas();
        int posicionRanking = 0;
        if (ranking != null) {
            for (int i = 0; i < ranking.size(); i++) {
                Long id = (Long) ranking.get(i)[0];
                if (id.equals(idPrenda)) {
                    posicionRanking = i + 1;
                    break;
                }
            }
        }

        // Tiempo promedio
        List<LocalDateTime> fechas = detalleRepos.fechasDeVenta(idPrenda);
        double tiempoPromedio = 0.0;
        if (fechas != null && fechas.size() >= 2) {
            long totalDias = 0;
            for (int i = 1; i < fechas.size(); i++) {
                totalDias += ChronoUnit.DAYS.between(fechas.get(i - 1), fechas.get(i));
            }
            tiempoPromedio = (double) totalDias / (fechas.size() - 1);
        }

        return new PrendaMetricasDTO(
                totalUnidadesVendidas,
                ingresos,
                cantVentas,
                ultimaVenta,
                gananciaAcumulada,
                ingresos - gananciaAcumulada,
                tiempoPromedio,
                posicionRanking
        );
    }

    @Override
    public List<TopPrendaDTO> top10Prendas() {

        List<Object[]> raw = metricaRepos.top10PrendasVendidasRaw();

        return raw.stream()
                .limit(10)
                .map(row -> {
                    Long prendaId = (Long) row[0];
                    Long vendidos = (Long) row[1];

                    Prenda p = prendaRepos.findById(prendaId).orElse(null);

                    if (p == null) return null;

                    return new TopPrendaDTO(
                            p.getIdPrenda(),
                            p.getCalidad(),
                            p.getMarca().getMarca(),
                            p.getMarca().getCategoria().getNombre(),
                            vendidos.intValue()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrendaOlvidadaDTO> listarPrendasOlvidadas() {
        return metricaRepos.listarPrendasOlvidadas();
    }
}
