package com.upc.ep.Controller;

import com.upc.ep.DTO.MarcaDTO;
import com.upc.ep.Services.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class MarcaController {
    @Autowired
    private MarcaService marcaService;

    @PostMapping("/post/marca")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MarcaDTO> crearMarca(@RequestBody MarcaDTO marcaDTO) {
        MarcaDTO nuevaMarca = marcaService.registrarMarca(marcaDTO);
        return new ResponseEntity<>(nuevaMarca, HttpStatus.CREATED);
    }

    @GetMapping("/get/marca")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<MarcaDTO>> listarMarcas() {
        List<MarcaDTO> marcas = marcaService.listarMarcas();
        return new ResponseEntity<>(marcas, HttpStatus.OK);
    }

    @PutMapping("/put/marca/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MarcaDTO> actualizarMarca(@PathVariable Long id, @RequestBody MarcaDTO marcaDTO) {
        MarcaDTO actualizada = marcaService.actualizarMarca(id, marcaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/delete/marca/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarMarca(@PathVariable Long id) {
        marcaService.eliminarMarca(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
