package com.upc.ep.Controller;

import com.upc.ep.DTO.MarcaRegistroDTO;
import com.upc.ep.DTO.MarcaResponseDTO;
import com.upc.ep.DTO.MarcaUpdateDTO;
import com.upc.ep.Services.MarcaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca")
public class MarcaController {
    @Autowired
    private MarcaService marcaService;

    @PostMapping("/crear/marca")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public MarcaResponseDTO registrarMarca(@Valid @RequestBody MarcaRegistroDTO dto) {
        return marcaService.registrarMarca(dto);
    }

    @GetMapping("/listar/marcas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcas() {
        return ResponseEntity.ok(marcaService.listarMarcas());
    }

    @PutMapping("/editar/marca/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarcaResponseDTO> editarMarca(@PathVariable Long id, @Valid @RequestBody MarcaUpdateDTO dto) {
        return ResponseEntity.ok(marcaService.editarMarca(id, dto));
    }

    @DeleteMapping("/desactivar/marca/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarMarca(@PathVariable Long id) {
        marcaService.desactivarMarca(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activar/marca/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activarMarca(@PathVariable Long id) {
        marcaService.activarMarca(id);
        return ResponseEntity.noContent().build();
    }
}