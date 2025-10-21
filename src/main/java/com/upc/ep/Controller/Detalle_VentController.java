package com.upc.ep.Controller;

import com.upc.ep.DTO.Detalle_VentDTO;
import com.upc.ep.Entidades.Detalle_Vent;
import com.upc.ep.Services.Detalle_VentService;
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
public class Detalle_VentController {
    @Autowired
    private Detalle_VentService dvService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/dv")
    public Detalle_VentDTO saveDV(@RequestBody Detalle_Vent dvDTO) {
        Detalle_Vent dv = modelMapper.map(dvDTO, Detalle_Vent.class);
        dv = dvService.saveDV(dv);
        return modelMapper.map(dv, Detalle_VentDTO.class);
    }

    @GetMapping("/dvs")
    public List<Detalle_VentDTO> listarDV() {
        List<Detalle_Vent> dvs = dvService.listarDV();
        return dvs.stream()
                .map(dv -> modelMapper.map(dv, Detalle_VentDTO.class))
                .collect(Collectors.toList());
    }
}
