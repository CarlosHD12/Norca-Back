package com.upc.ep.Controller;

import com.upc.ep.DTO.CategoriaDTO;
import com.upc.ep.Entidades.Categoria;
import com.upc.ep.Services.CategoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Norca")
/*@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)*/
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/categoria")
    //@PreAuthorize("hasRole('ADMIN')")
    public CategoriaDTO saveCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
        categoria = categoriaService.saveCategoria(categoria);
        return modelMapper.map(categoria, CategoriaDTO.class);
    }

    @GetMapping("/categorias")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public List<CategoriaDTO> listarCategorias() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        return categorias.stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
    }
}
