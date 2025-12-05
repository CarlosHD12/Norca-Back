package com.upc.ep.Controller;

import com.upc.ep.DTO.PrendaMetricasDTO;
import com.upc.ep.DTO.PrendaOlvidadaDTO;
import com.upc.ep.DTO.TopPrendaDTO;
import com.upc.ep.Services.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class MetricasController {
    @Autowired
    private MetricasService metricasService;

    @GetMapping("/prenda/metrica/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<PrendaMetricasDTO> obtenerMetricas(
            @PathVariable Long idPrenda) {
        PrendaMetricasDTO metricas = metricasService.obtenerMetricas(idPrenda);
        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/top10")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<TopPrendaDTO> obtenerTop10() {
        return metricasService.top10Prendas();
    }

    @GetMapping("/olvidadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<PrendaOlvidadaDTO>> listarPrendasOlvidadas() {
        return ResponseEntity.ok(metricasService.listarPrendasOlvidadas());
    }
}
