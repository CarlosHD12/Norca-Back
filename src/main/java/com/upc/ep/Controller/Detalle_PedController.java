package com.upc.ep.Controller;

import com.upc.ep.DTO.Detalle_PedDTO;
import com.upc.ep.Entidades.Detalle_Ped;
import com.upc.ep.Services.Detalle_PedService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Detalle_PedController {
    @Autowired
    private Detalle_PedService detallePedService;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/dp")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public Detalle_PedDTO saveDetalle(@RequestBody Detalle_PedDTO detalleDTO) {
        Detalle_Ped detalle = modelMapper.map(detalleDTO, Detalle_Ped.class);
        detalle = detallePedService.saveDetallePed(detalle);
        return modelMapper.map(detalle, Detalle_PedDTO.class);
    }

    // -------------------- ACTUALIZAR --------------------
    @PutMapping("/dp/modificar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public Detalle_PedDTO actualizarDetalle(@PathVariable Long id, @RequestBody Detalle_PedDTO detalleDTO) {
        Detalle_Ped detalle = modelMapper.map(detalleDTO, Detalle_Ped.class);
        detalle = detallePedService.actualizarDetalle(id, detalle);
        return modelMapper.map(detalle, Detalle_PedDTO.class);
    }

    // -------------------- LISTAR TODOS --------------------
    @GetMapping("/dps")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<Detalle_PedDTO> listarDetalles() {
        return detallePedService.listarDetalles()
                .stream()
                .map(d -> modelMapper.map(d, Detalle_PedDTO.class))
                .toList();
    }

    // -------------------- LISTAR POR PEDIDO --------------------
    @GetMapping("/dp/pedido/{idPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public List<Detalle_PedDTO> listarPorPedido(@PathVariable Long idPedido) {
        return detallePedService.listarPorPedido(idPedido)
                .stream()
                .map(d -> modelMapper.map(d, Detalle_PedDTO.class))
                .toList();
    }

    // -------------------- CONTAR PRNDAS POR PEDIDO --------------------
    @GetMapping("/dp/pedido/{idPedido}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public Integer contarPrendasPedido(@PathVariable Long idPedido) {
        return detallePedService.contarPrendasPedido(idPedido);
    }

    // -------------------- ELIMINAR --------------------
    @DeleteMapping("/dp/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        boolean eliminado = detallePedService.eliminarDetalle(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}