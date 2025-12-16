package com.upc.ep.Controller;

import com.upc.ep.DTO.MarcaDTO;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Services.MarcaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MarcaController {
    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/marca")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public MarcaDTO saveMarca(@RequestBody MarcaDTO marcaDTO) {
        Marca marca = modelMapper.map(marcaDTO, Marca.class);
        marca = marcaService.saveMarca(marca);
        return modelMapper.map(marca, MarcaDTO.class);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/marcas")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<MarcaDTO> listarMarcas() {
        List<Marca> marcas = marcaService.listarMarcas();
        return marcas.stream()
                .map(marca -> modelMapper.map(marca, MarcaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR CATEGORIA --------------------
    @GetMapping("/marcas/categoria/{idCategoria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<MarcaDTO> listarPorCategoria(@PathVariable Long idCategoria) {
        List<Marca> marcas = marcaService.listarPorCategoria(idCategoria);
        return marcas.stream()
                .map(marca -> modelMapper.map(marca, MarcaDTO.class))
                .collect(Collectors.toList());
    }
}
