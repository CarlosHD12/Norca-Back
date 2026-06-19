package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.MetricaVentaDTO;
import com.upc.ep.Entidades.Metrica;
import com.upc.ep.Repositorio.MetricaRepos;
import com.upc.ep.Services.MetricaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricaIMPL implements MetricaService {

    @Autowired
    private MetricaRepos metricaRepos;

    @Override
    public MetricaVentaDTO obtenerMetricaPorPrenda(Long idPrenda) {

        Metrica metrica = metricaRepos.obtenerPorIdPrenda(idPrenda)
                .orElseThrow(() ->
                        new RuntimeException("No se encontraron métricas para la prenda con id: " + idPrenda));

        return MetricaVentaDTO.builder()
                .idMetrica(metrica.getIdMetrica())
                .idPrenda(metrica.getPrenda().getIdPrenda())
                .unidadesVendidas(metrica.getUnidadesVendidas())
                .ingresosTotales(metrica.getIngresosTotales())
                .gananciaAcumulada(metrica.getGananciaAcumulada())
                .inversionTotal(metrica.getInversionTotal())
                .ventasRealizadas(metrica.getVentasRealizadas())
                .ultimaVenta(metrica.getUltimaVenta())
                .tiempoPromedioEntreVentas(metrica.getTiempoPromedioEntreVentas())
                .build();
    }
}
