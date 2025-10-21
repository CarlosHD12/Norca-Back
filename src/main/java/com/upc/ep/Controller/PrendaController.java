package com.upc.ep.Controller;

import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Services.PrendaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class PrendaController {
    @Autowired
    private PrendaService prendaService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/prenda")
    public PrendaDTO savePrenda(@RequestBody PrendaDTO prendaDTO) {
        Prenda prenda = modelMapper.map(prendaDTO, Prenda.class);
        prenda = prendaService.savePrenda(prenda);
        return modelMapper.map(prenda, PrendaDTO.class);
    }

    @GetMapping("/prendas")
    public List<PrendaDTO> listarPrendas() {
        List<Prenda> prendas = prendaService.listarPrendas  ();
        return prendas.stream()
                .map(prenda -> modelMapper.map(prenda, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/prenda/modificar/{id}")
    public ResponseEntity<PrendaDTO> putPrenda(@PathVariable Long id, @RequestBody PrendaDTO prendaDTO) {
        PrendaDTO actualizada = prendaService.putPrenda(id, prendaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }
}
