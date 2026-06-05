package com.upc.ep.Controller;

import com.upc.ep.DTO.TallaRegistroDTO;
import com.upc.ep.DTO.TallaResponseDTO;
import com.upc.ep.DTO.TallaUpdateDTO;
import com.upc.ep.Services.TallaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca")
public class TallaController {
    @Autowired
    private TallaService tallaService;

    @PostMapping("/crear/talla")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public TallaResponseDTO registrarTalla(@Valid @RequestBody TallaRegistroDTO dto) {
        return tallaService.registrarTalla(dto);
    }

    @GetMapping("/listar/tallas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<TallaResponseDTO>> listarTallas() {
        return ResponseEntity.ok(tallaService.listarTallas());
    }

    @PutMapping("/editar/talla/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TallaResponseDTO> editarTalla(@PathVariable Long id, @Valid @RequestBody TallaUpdateDTO dto) {
        return ResponseEntity.ok(tallaService.editarTalla(id, dto));
    }

    @DeleteMapping("/desactivar/talla/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarTalla(@PathVariable Long id) {
        tallaService.desactivarTalla(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activar/talla/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activarTalla(@PathVariable Long id) {
        tallaService.activarTalla(id);
        return ResponseEntity.noContent().build();
    }
}