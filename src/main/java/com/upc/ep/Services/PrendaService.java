package com.upc.ep.Services;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Prenda;

import java.util.List;
import java.util.Map;

public interface PrendaService {
    PrendaDTO registrarPrenda(PrendaDTO prendaDTO);
    PrendaDTO editarPrenda(Long id, PrendaDTO prendaDTO);
    void eliminarPrenda(Long id);
    Prenda obtenerPrendaPorId(Long idPrenda);
    List<PrendaListadoDTO> listarPrendasConStockYUltimoPrecio();
    PrendaDetalleDTO obtenerDetallePrenda(Long id);
    void cambiarEstado(Long idPrenda);
    void activarPrenda(Long idPrenda);
    List<PrendaCarritoDTO> listarPrendasDisponibles();
    List<InventarioActivoDTO> listarInventarioPorPrenda(Long idPrenda);
    List<Map<String, Object>> distribucionPorCategoria();
    List<Map<String, Object>> distribucionPorMarca();
    List<Map<String, Object>> distribucionPorEstado();
    List<PrendaOlvidadaDTO> obtenerPrendasOlvidadas();
    List<TopDTO> rankingPrendasMasVendidas();
    List<StockBajoDTO> bajoStock(Integer limite);
    PrendasTotalesDTO obtenerKPIPrendas();
    Long obtenerPrendasAgotadas();
    List<StockCategoriaDTO> obtenerStockPorCategoria();
    List<PrendaDTO> obtenerPrendas();
}