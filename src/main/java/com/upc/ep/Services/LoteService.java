package com.upc.ep.Services;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Lote;

import java.util.List;

public interface LoteService {
    LoteResponseDTO registrarLote(LoteRegistroDTO dto);
    MetricaLoteDTO obtenerMetricasLote(Long idLote);
    LoteSeleccionadoDTO obtenerInventariosDisponibles(Long idPrenda);
    void actualizarStockLote(Long idLote);
    HistorialPrendaResponseDTO listarHistorialLotes(Long idPrenda);
    Lote obtenerLoteFIFOActivo(Long idPrenda);
    UltimoLoteResponseDTO obtenerUltimoLotePrenda(Long idPrenda);
    ResumenLoteDTO obtenerResumenLote(Long idLote);

//    LoteMetricasDTO calcularMetricas(Long idLote);
//    Long obtenerLotesActivos();
//    List<LoteMensualDTO> obtenerLotesPorMes();
//    LotesTotalesDTO obtenerStockDisponible();
//    List<LoteDetalleDTO> obtenerHistorialPrenda(Long idPrenda);
}
