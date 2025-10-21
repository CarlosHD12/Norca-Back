package com.upc.ep.Controller;

import com.upc.ep.DTO.Detalle_PedDTO;
import com.upc.ep.Entidades.Detalle_Ped;
import com.upc.ep.Services.Detalle_PedService;
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
public class Detalle_PedController {
    @Autowired
    private Detalle_PedService dpService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/dp")
    public Detalle_PedDTO saveDP(@RequestBody Detalle_PedDTO dpDTO) {
        Detalle_Ped dp = modelMapper.map(dpDTO, Detalle_Ped.class);
        dp = dpService.saveDP(dp);
        return modelMapper.map(dp, Detalle_PedDTO.class);
    }

    @GetMapping("/dps")
    public List<Detalle_PedDTO> listarDP() {
        List<Detalle_Ped> dps = dpService.listarDP();
        return dps.stream()
                .map(dp -> modelMapper.map(dp, Detalle_PedDTO.class))
                .collect(Collectors.toList());
    }
}
