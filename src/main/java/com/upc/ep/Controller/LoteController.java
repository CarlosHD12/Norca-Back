package com.upc.ep.Controller;

import com.upc.ep.DTO.LoteDTO;
import com.upc.ep.Services.LoteService;
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
public class LoteController {
    @Autowired
    private LoteService loteService;

    @GetMapping("/lotes/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<LoteDTO>> obtenerLotesPorPrenda(@PathVariable Long idPrenda) {
        List<LoteDTO> lotes = loteService.obtenerLotesPorPrenda(idPrenda);
        return ResponseEntity.ok(lotes);
    }
}
