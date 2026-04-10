package com.upc.ep.Controller;

import com.upc.ep.DTO.MetricaDTO;
import com.upc.ep.Services.MetricaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class MetricaController {
    @Autowired
    private MetricaService metricaService;

    @GetMapping("/metrica/prenda/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MetricaDTO> getMetrica(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(metricaService.obtenerMetricaPorPrenda(idPrenda));
    }

    @GetMapping("/prenda/{idPrenda}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Boolean> existeMetrica(@PathVariable Long idPrenda) {
        boolean existe = metricaService.existeMetricaPorPrenda(idPrenda);
        return ResponseEntity.ok(existe);
    }
}
