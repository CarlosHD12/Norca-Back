package com.upc.ep.Controller;

import com.upc.ep.DTO.TallaDTO;
import com.upc.ep.Services.TallaService;
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
public class TallaController {
    @Autowired
    private TallaService tallaService;

    @PostMapping("/post/talla")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<TallaDTO> crearTalla(@RequestBody TallaDTO tallaDTO) {
        TallaDTO nuevaTalla = tallaService.registrarTalla(tallaDTO);
        return new ResponseEntity<>(nuevaTalla, HttpStatus.CREATED);
    }

    @GetMapping("/get/talla")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<TallaDTO>> listarTallas() {
        List<TallaDTO> tallas = tallaService.listarTallas();
        return new ResponseEntity<>(tallas, HttpStatus.OK);
    }

    @PutMapping("/put/talla/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<TallaDTO> actualizarTalla(@PathVariable Long id, @RequestBody TallaDTO tallaDTO) {
        TallaDTO actualizada = tallaService.actualizarTalla(id, tallaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/delete/talla/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarTalla(@PathVariable Long id) {
        tallaService.eliminarTalla(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}