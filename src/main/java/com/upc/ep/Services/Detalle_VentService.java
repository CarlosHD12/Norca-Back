package com.upc.ep.Services;

import com.upc.ep.DTO.Detalle_VentDTO;
import com.upc.ep.Entidades.Detalle_Vent;

import java.time.LocalDateTime;
import java.util.List;

public interface Detalle_VentService {
    Detalle_VentDTO saveDetalleVenta(Detalle_VentDTO detalleDTO);
    List<Detalle_VentDTO> listarDetalles();
    List<Detalle_VentDTO> listarPorPrenda(Long idPrenda);
    boolean eliminarDetalle(Long id);
    Detalle_VentDTO actualizarDetalle(Long id, Detalle_VentDTO dto);
    Integer contarDetallesPorVenta(Long idVenta);
    List<Detalle_Vent> listarPorVenta(Long idVenta);
    Integer totalUnidadesVendidas(Long idPrenda);
    Double ingresosTotales(Long idPrenda);
    Integer cantidadVentas(Long idPrenda);
    LocalDateTime ultimaVenta(Long idPrenda);
    List<Object[]> rankingPrendas();
    Long tiempoPromedioVenta(Long idPrenda);
}
