package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Lote;
import com.upc.ep.Services.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class LoteController {
    @Autowired
    private LoteService loteService;

    @PostMapping("/post/lote")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<LoteDTO> registrarLote(@RequestBody LoteDTO loteDTO) {
        LoteDTO nuevoLote = loteService.registrarLote(loteDTO);
        return new ResponseEntity<>(nuevoLote, HttpStatus.CREATED);
    }

    @GetMapping("/metricas/lote/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<LoteMetricasDTO> getMetricas(@PathVariable Long id) {
        LoteMetricasDTO metricas = loteService.calcularMetricas(id);
        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/historial/lote/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<LoteDetalleDTO>> obtenerHistorialPrenda(@PathVariable Long idPrenda) {
        List<LoteDetalleDTO> historial = loteService.obtenerHistorialPrenda(idPrenda);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/stock/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public LotesTotalesDTO obtenerStockDisponible(){
        return loteService.obtenerStockDisponible();
    }

    @GetMapping("/lote/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public Long obtenerLotesActivos() {
        return loteService.obtenerLotesActivos();
    }

    @GetMapping("/lote/mensual")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<LoteMensualDTO> obtenerLotesPorMes() {
        return loteService.obtenerLotesPorMes();
    }
}
