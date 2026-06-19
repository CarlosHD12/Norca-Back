package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Services.LoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca")
public class LoteController {
    @Autowired
    private LoteService loteService;

    @PostMapping("/crear/lote")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<LoteResponseDTO> registrarLote(@Valid @RequestBody LoteRegistroDTO dto) {
        LoteResponseDTO response = loteService.registrarLote(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/metricas/lote/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MetricaLoteDTO> obtenerMetricasLote(@PathVariable Long id) {
        return ResponseEntity.ok(
                loteService.obtenerMetricasLote(id)
        );
    }

    @GetMapping("/inventarios/loteFIFO/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<LoteSeleccionadoDTO> obtenerInventariosDisponibles(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(loteService.obtenerInventariosDisponibles(idPrenda)
        );
    }

    @GetMapping("/historial/lote/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<HistorialPrendaResponseDTO> listarHistorialLotes(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(loteService.listarHistorialLotes(idPrenda));
    }

    @GetMapping("/ultimo/lote/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<UltimoLoteResponseDTO> obtenerUltimoLotePrenda(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(loteService.obtenerUltimoLotePrenda(idPrenda));
    }

//    @GetMapping("/historial/lote/{idPrenda}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public ResponseEntity<List<LoteDetalleDTO>> obtenerHistorialPrenda(@PathVariable Long idPrenda) {
//        List<LoteDetalleDTO> historial = loteService.obtenerHistorialPrenda(idPrenda);
//        return ResponseEntity.ok(historial);
//    }
//
//    @GetMapping("/stock/total")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public LotesTotalesDTO obtenerStockDisponible(){
//        return loteService.obtenerStockDisponible();
//    }
//
//    @GetMapping("/lote/activos")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public Long obtenerLotesActivos() {
//        return loteService.obtenerLotesActivos();
//    }
//
//    @GetMapping("/lote/mensual")
//    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
//    public List<LoteMensualDTO> obtenerLotesPorMes() {
//        return loteService.obtenerLotesPorMes();
//    }
}
