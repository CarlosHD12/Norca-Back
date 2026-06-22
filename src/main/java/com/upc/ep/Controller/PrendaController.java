package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Services.PrendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/Norca")
public class PrendaController {
    @Autowired
    private PrendaService prendaService;

    @PostMapping("/crear/prenda")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaResponseDTO> registrarPrenda(@Valid @RequestBody PrendaRegistroDTO dto) {
        PrendaResponseDTO response = prendaService.registrarPrenda(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/editar/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<PrendaResponseDTO> actualizarPrenda(
            @PathVariable Long id,
            @Valid @RequestBody PrendaUpdateDTO dto) {

        System.out.println("ENTRO AL CONTROLLER EDITAR");

        PrendaResponseDTO response =
                prendaService.actualizarPrenda(id, dto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/inhabilitar/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> inhabilitarPrenda(@PathVariable Long id) {
        prendaService.inhabilitarPrenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaDetalleDTO> obtenerDetallePrenda(@PathVariable Long id) {
        return ResponseEntity.ok(prendaService.obtenerDetallePrenda(id));
    }

    @GetMapping("/listar/prendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Page<PrendaListResponseDTO>> listarPrendas(
            @RequestParam(required = false)
            String search,
            @RequestParam(required = false)
            String categoria,
            @RequestParam(required = false)
            String marca,
            @RequestParam(required = false)
            String estado,
            @RequestParam(required = false)
            Integer stockMin,
            @RequestParam(required = false)
            Integer stockMax,
            @RequestParam(required = false)
            BigDecimal precioMin,
            @RequestParam(required = false)
            BigDecimal precioMax,
            @PageableDefault(size = 20)
            Pageable pageable) {
        return ResponseEntity.ok(prendaService.listarPrendasFiltradas(
                                search,
                                categoria,
                                marca,
                                estado,
                                stockMin,
                                stockMax,
                                precioMin,
                                precioMax,
                                pageable));
    }


    @PutMapping("/activar/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> activarPrenda(@PathVariable Long id) {
        prendaService.activarPrenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/kpis/prendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaKpiResponseDTO> obtenerKpis() {
        return ResponseEntity.ok(prendaService.obtenerKpis());
    }

    @GetMapping("/detalle/rapido/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaQuickDetalleDTO> obtenerDetalleRapido(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(prendaService.obtenerDetalleRapido(idPrenda));
    }


//    @GetMapping("/listar/prenda/disponibles")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<PrendaCarritoDTO> listarPrendasDisponibles() {
//        return prendaService.listarPrendasDisponibles();
//    }
//
//    @GetMapping("/inventario/prenda/{idPrenda}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<InventarioActivoDTO>> listarInventarioPorPrenda(@PathVariable Long idPrenda) {
//        List<InventarioActivoDTO> inventarios = prendaService.listarInventarioPorPrenda(idPrenda);
//        return ResponseEntity.ok(inventarios);
//    }
//
//    @GetMapping("/distribucion/categoria")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorCategoria() {
//        return ResponseEntity.ok(prendaService.distribucionPorCategoria());
//    }
//
//    @GetMapping("/distribucion/marca")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorMarca() {
//        return ResponseEntity.ok(prendaService.distribucionPorMarca());
//    }
//
//    @GetMapping("/distribucion/estado")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorEstado() {
//        return ResponseEntity.ok(prendaService.distribucionPorEstado());
//    }
//
//    @GetMapping("/prenda/olvidada")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<PrendaOlvidadaDTO>> obtenerPrendasOlvidadas() {
//        List<PrendaOlvidadaDTO> lista = prendaService.obtenerPrendasOlvidadas();
//        return ResponseEntity.ok(lista);
//    }
//
//    @GetMapping("/prenda/ranking")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<TopDTO> rankingMasVendidas(){
//        return prendaService.rankingPrendasMasVendidas();
//    }
//
//    @GetMapping("/prenda/stock-bajo")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<StockBajoDTO> obtenerPrendasBajoStock(
//            @RequestParam(defaultValue = "10") Integer limite) {
//        return prendaService.bajoStock(limite);
//    }
//
//    @GetMapping("/prendas/totales")
//    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
//    public PrendasTotalesDTO obtenerKPIPrendas() {
//        return prendaService.obtenerKPIPrendas();
//    }
//
//    @GetMapping("/prenda/agotada")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public Long obtenerPrendasAgotadas() {
//        return prendaService.obtenerPrendasAgotadas();
//    }
//
//    @GetMapping("/stock/categoria")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<StockCategoriaDTO> stockPorCategoria() {
//        return prendaService.obtenerStockPorCategoria();
//    }
//
//    @GetMapping("/get/prendas")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<PrendaDTO> getPrendas() {
//        return prendaService.obtenerPrendas();
//    }
}