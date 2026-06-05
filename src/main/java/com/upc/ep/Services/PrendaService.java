package com.upc.ep.Services;

import com.upc.ep.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PrendaService {
    PrendaResponseDTO registrarPrenda(PrendaRegistroDTO dto);
    PrendaResponseDTO actualizarPrenda(Long idPrenda, PrendaUpdateDTO dto);
    void eliminarPrenda(Long id);
    PrendaDetalleDTO obtenerDetallePrenda(Long idPrenda);
    void validarPrendaAgotada(Long idPrenda);
    Page<PrendaListResponseDTO> listarPrendasFiltradas(
            String search,
            String categoria,
            String marca,
            String estado,
            Integer stockMin,
            Integer stockMax,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Pageable pageable
    );
    void activarPrenda(Long idPrenda);
    PrendaKpiResponseDTO obtenerKpis();
    PrendaQuickDetalleDTO obtenerDetalleRapido(Long idPrenda);


//    Prenda obtenerPrendaPorId(Long idPrenda);
//    List<PrendaListadoDTO> listarPrendasConStockYUltimoPrecio();
//    void cambiarEstado(Long idPrenda);
//    void activarPrenda(Long idPrenda);
//    List<PrendaCarritoDTO> listarPrendasDisponibles();
//    List<InventarioActivoDTO> listarInventarioPorPrenda(Long idPrenda);
//    List<Map<String, Object>> distribucionPorCategoria();
//    List<Map<String, Object>> distribucionPorMarca();
//    List<Map<String, Object>> distribucionPorEstado();
//    List<PrendaOlvidadaDTO> obtenerPrendasOlvidadas();
//    List<TopDTO> rankingPrendasMasVendidas();
//    List<StockBajoDTO> bajoStock(Integer limite);
//    PrendasTotalesDTO obtenerKPIPrendas();
//    Long obtenerPrendasAgotadas();
//    List<StockCategoriaDTO> obtenerStockPorCategoria();
//    List<PrendaDTO> obtenerPrendas();
}