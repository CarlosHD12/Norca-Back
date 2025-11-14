package com.upc.ep.Controller;

import com.upc.ep.DTO.TallaDTO;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Services.TallaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/talla")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public TallaDTO saveTalla(@RequestBody TallaDTO tallaDTO) {
        Talla talla = modelMapper.map(tallaDTO, Talla.class);
        talla = tallaService.saveTalla(talla);
        return modelMapper.map(talla, TallaDTO.class);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/tallas")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<TallaDTO> listarTallas() {
        List<Talla> tallas = tallaService.listarTallas();
        return tallas.stream()
                .map(t -> modelMapper.map(t, TallaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR PRENDA --------------------
    @GetMapping("/tallas/prenda/{idPrenda}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<TallaDTO> listarPorPrenda(@PathVariable Long idPrenda) {
        List<Talla> tallas = tallaService.listarPorPrenda(idPrenda);
        return tallas.stream()
                .map(t -> modelMapper.map(t, TallaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- EDITAR --------------------
    @PutMapping("/talla/modificar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<TallaDTO> editarTalla(@PathVariable Long id, @RequestBody TallaDTO tallaDTO) {
        Talla tallaActualizada = tallaService.editarTalla(id, modelMapper.map(tallaDTO, Talla.class));
        return new ResponseEntity<>(modelMapper.map(tallaActualizada, TallaDTO.class), HttpStatus.OK);
    }

    // -------------------- ELIMINAR --------------------
    @DeleteMapping("/talla/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarTalla(@PathVariable Long id) {
        boolean ok = tallaService.eliminarTalla(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
