package com.upc.ep.Controller;

import com.upc.ep.DTO.CategoriaDTO;
import com.upc.ep.Entidades.Categoria;
import com.upc.ep.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/post/categoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<CategoriaDTO> crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO nuevaCategoria = categoriaService.registrarCategoria(categoriaDTO);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @GetMapping("/get/categoria")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        List<CategoriaDTO> categorias = categoriaService.listarCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @PutMapping("/put/categoria/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO actualizada = categoriaService.actualizarCategoria(id, categoriaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/delete/categoria/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/post/png/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<?> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        categoriaService.guardarImagen(id, file);
        return ResponseEntity.ok("Imagen subida");
    }

    @GetMapping("/categoria/png/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        byte[] imagen = categoriaService.obtenerImagen(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagen);
    }
}