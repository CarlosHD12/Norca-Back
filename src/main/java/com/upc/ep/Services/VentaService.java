package com.upc.ep.Services;

import com.upc.ep.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VentaService {
    VentaDTO registrarVenta(VentaDTO ventaDTO);
    List<VentaDTO> listarVentas();
    VentaDTO editarVenta(Long id, VentaDTO ventaDTO);
    void eliminarVenta(Long id);
    VentaDetalleDTO obtenerDetalleVenta(Long idVenta);
    void desactivarVenta(Long idVenta);
    VentasTotalesDTO obtenerKpiVentas();
    VentasTotalesDTO obtenerKpiUnidades();
    VentasTotalesDTO obtenerIngresosTotales();
    MetodoPagoDTO obtenerMetodoPagoFavorito();
    List<IngresosCategoriaDTO> obtenerIngresosPorCategoria();
    List<Map<String, Object>> reportePorMes(String tipo);
    Page<VentaListadoDTO> listarVentas(
            String codigo,
            String metodoPago,
            String periodo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Double precioMin,
            Double precioMax,
            Integer unidadesMin,
            Integer unidadesMax,
            Pageable pageable
    );

    ImpactoVentaDTO obtenerImpactoVenta(Long idVenta);
}