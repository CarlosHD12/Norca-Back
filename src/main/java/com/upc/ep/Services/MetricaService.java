package com.upc.ep.Services;

import com.upc.ep.DTO.MetricaVentaDTO;

public interface MetricaService {
    MetricaVentaDTO obtenerMetricaPorPrenda(Long idPrenda);
}
