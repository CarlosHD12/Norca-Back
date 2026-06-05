package com.upc.ep.Controller;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;
import com.upc.ep.Services.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Norca")
public class MovimientoController {
    @Autowired
    private MovimientoService movimientoService;

    @PostMapping("/movimiento")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MovimientoResponseDTO>
    registrarMovimiento(@Valid @RequestBody MovimientoRegistroDTO dto) {
        MovimientoResponseDTO response = movimientoService.registrarMovimiento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
