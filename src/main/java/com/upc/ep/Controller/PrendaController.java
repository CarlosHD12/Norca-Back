package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Services.PrendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = {
                "http://localhost:4200",
                "https://norca-back-production.up.railway.app"
        },
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class PrendaController {
    @Autowired
    private PrendaService prendaService;

    @PostMapping("/post/prenda")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaDTO> registrarPrenda(@RequestBody PrendaDTO prendaDTO) {
        PrendaDTO nuevaPrenda = prendaService.registrarPrenda(prendaDTO);
        return new ResponseEntity<>(nuevaPrenda, HttpStatus.CREATED);
    }

    @PutMapping("/put/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaDTO> editarPrenda(@PathVariable Long id, @RequestBody PrendaDTO prendaDTO) {
        PrendaDTO actualizado = prendaService.editarPrenda(id, prendaDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/delete/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarPrenda(@PathVariable Long id) {
        prendaService.eliminarPrenda(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/obtener/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Prenda> getPrenda(@PathVariable Long id) {
        Prenda prenda = prendaService.obtenerPrendaPorId(id);
        return ResponseEntity.ok(prenda);
    }

    @GetMapping("/listar/prendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<PrendaListadoDTO> listarPrendas() {
        return prendaService.listarPrendasConStockYUltimoPrecio();
    }

    @GetMapping("/detalle/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(prendaService.obtenerDetallePrenda(id));
    }

    @PutMapping("/cambiar/estado/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public String cambiarEstado(@PathVariable Long idPrenda) {
        prendaService.cambiarEstado(idPrenda);
        return "Prenda con id " + idPrenda + " marcada como fuera de temporada";
    }

    @PutMapping("/activar/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<String> activarPrenda(@PathVariable Long id) {
        prendaService.activarPrenda(id);
        return ResponseEntity.ok("Prenda activada correctamente");
    }

    @GetMapping("/listar/prenda/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<PrendaCarritoDTO> listarPrendasDisponibles() {
        return prendaService.listarPrendasDisponibles();
    }

    @GetMapping("/inventario/prenda/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<InventarioActivoDTO>> listarInventarioPorPrenda(@PathVariable Long idPrenda) {
        List<InventarioActivoDTO> inventarios = prendaService.listarInventarioPorPrenda(idPrenda);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/distribucion/categoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorCategoria() {
        return ResponseEntity.ok(prendaService.distribucionPorCategoria());
    }

    @GetMapping("/distribucion/marca")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorMarca() {
        return ResponseEntity.ok(prendaService.distribucionPorMarca());
    }

    @GetMapping("/distribucion/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<Map<String, Object>>> getDistribucionPorEstado() {
        return ResponseEntity.ok(prendaService.distribucionPorEstado());
    }

    @GetMapping("/prenda/olvidada")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<PrendaOlvidadaDTO>> obtenerPrendasOlvidadas() {
        List<PrendaOlvidadaDTO> lista = prendaService.obtenerPrendasOlvidadas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/prenda/ranking")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<TopDTO> rankingMasVendidas(){
        return prendaService.rankingPrendasMasVendidas();
    }

    @GetMapping("/prenda/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<StockBajoDTO> obtenerPrendasBajoStock(
            @RequestParam(defaultValue = "10") Integer limite) {
        return prendaService.bajoStock(limite);
    }

    @GetMapping("/prendas/totales")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public PrendasTotalesDTO obtenerKPIPrendas() {
        return prendaService.obtenerKPIPrendas();
    }

    @GetMapping("/prenda/agotada")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public Long obtenerPrendasAgotadas() {
        return prendaService.obtenerPrendasAgotadas();
    }

    @GetMapping("/stock/categoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<StockCategoriaDTO> stockPorCategoria() {
        return prendaService.obtenerStockPorCategoria();
    }

    @GetMapping("/get/prendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<PrendaDTO> getPrendas() {
        return prendaService.obtenerPrendas();
    }
}