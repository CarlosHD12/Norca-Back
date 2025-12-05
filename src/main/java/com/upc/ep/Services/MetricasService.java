package com.upc.ep.Services;

import com.upc.ep.DTO.PrendaMetricasDTO;
import com.upc.ep.DTO.PrendaOlvidadaDTO;
import com.upc.ep.DTO.TopPrendaDTO;

import java.util.List;

public interface MetricasService {
    PrendaMetricasDTO obtenerMetricas(Long idPrenda);
    List<TopPrendaDTO> top10Prendas();
    List<PrendaOlvidadaDTO> listarPrendasOlvidadas();
}
