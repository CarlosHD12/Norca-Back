package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.MetricaDTO;
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
    public MetricaDTO obtenerMetricaPorPrenda(Long idPrenda) {
        Metrica m = metricaRepos.findByPrenda_IdPrenda(idPrenda)
                .orElseThrow(() -> new RuntimeException("Métrica no encontrada"));
        MetricaDTO dto = new MetricaDTO();
        dto.setIdMetrica(m.getIdMetrica());
        dto.setPrendaId(m.getPrenda().getIdPrenda());
        dto.setUnidadesVendidas(m.getUnidadesVendidas());
        dto.setIngresosTotales(m.getIngresosTotales());
        dto.setGananciaAcumulada(m.getGananciaAcumulada());
        dto.setInversionTotal(m.getInversionTotal());
        dto.setVentasRealizadas(m.getVentasRealizadas());
        dto.setUltimaVenta(m.getUltimaVenta());
        dto.setTiempoPromedioEntreVentas(m.getTiempoPromedioEntreVentas());
        dto.setRoi(m.getRoi());
        dto.setRanking(m.getRanking());
        return dto;
    }

    @Override
    public boolean existeMetricaPorPrenda(Long idPrenda) {
        return metricaRepos.existsByPrenda_IdPrenda(idPrenda);
    }
}
