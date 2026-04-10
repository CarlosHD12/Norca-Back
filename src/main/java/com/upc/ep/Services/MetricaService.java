package com.upc.ep.Services;

import com.upc.ep.DTO.MetricaDTO;

public interface MetricaService {
    MetricaDTO obtenerMetricaPorPrenda(Long idPrenda);
    boolean existeMetricaPorPrenda(Long idPrenda);
}
