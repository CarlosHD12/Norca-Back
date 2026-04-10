package com.upc.ep.Services;

import com.upc.ep.DTO.*;

import java.util.List;

public interface LoteService {
    LoteDTO registrarLote(LoteDTO loteDTO);
    LoteMetricasDTO calcularMetricas(Long idLote);
    Long obtenerLotesActivos();
    List<LoteMensualDTO> obtenerLotesPorMes();
    LotesTotalesDTO obtenerStockDisponible();
    List<LoteDetalleDTO> obtenerHistorialPrenda(Long idPrenda);
}
