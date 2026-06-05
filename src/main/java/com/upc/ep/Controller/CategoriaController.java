package com.upc.ep.Controller;

import com.upc.ep.DTO.CategoriaRegistroDTO;
import com.upc.ep.DTO.CategoriaResponseDTO;
import com.upc.ep.DTO.CategoriaUpdateDTO;
import com.upc.ep.Services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/crear/categoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public CategoriaResponseDTO registrarCategoria(@Valid @RequestBody CategoriaRegistroDTO dto) {
        return categoriaService.registrarCategoria(dto);
    }

    @GetMapping("/listar/categorias")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @PutMapping("/editar/categoria/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> editarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaUpdateDTO dto) {
        return ResponseEntity.ok(categoriaService.editarCategoria(id, dto));
    }

    @DeleteMapping("/desactivar/categoria/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long id) {
        categoriaService.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activar/categoria/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activarCategoria(@PathVariable Long id) {
        categoriaService.activarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}