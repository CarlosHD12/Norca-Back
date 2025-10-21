package com.upc.ep.Controller;

import com.upc.ep.DTO.ModeloDTO;
import com.upc.ep.Entidades.Modelo;
import com.upc.ep.Services.ModeloService;
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
public class ModeloController {
    @Autowired
    private ModeloService modeloService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/modelo")
    public ModeloDTO saveModelo(@RequestBody ModeloDTO modeloDTO) {
        Modelo modelo = modelMapper.map(modeloDTO, Modelo.class);
        modelo = modeloService.saveModelo(modelo);
        return modelMapper.map(modelo, ModeloDTO.class);
    }

    @GetMapping("/modelos")
    public List<ModeloDTO> listaModelos() {
        List<Modelo> modelos = modeloService.listarModelos();
        return modelos.stream()
                .map(modelo -> modelMapper.map(modelo, ModeloDTO.class))
                .collect(Collectors.toList());
    }
}
