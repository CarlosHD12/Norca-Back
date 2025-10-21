package com.upc.ep.Controller;

import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Services.VentaService;
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
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/venta")
    public VentaDTO saveVenta(@RequestBody VentaDTO ventaDTO) {
        Venta venta = modelMapper.map(ventaDTO, Venta.class);
        venta = ventaService.saveVenta(venta);
        return modelMapper.map(venta, VentaDTO.class);
    }

    @GetMapping("/ventas")
    public List<VentaDTO> listarVentas() {
        return ventaService.listarVentas(); // ya devuelve DTOs
    }

    @PutMapping("/venta/modificar/{id}")
    public ResponseEntity<VentaDTO> putVenta(@PathVariable Long id, @RequestBody VentaDTO ventaDTO) {
        VentaDTO actualizada = ventaService.putVenta(id, ventaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }
}
