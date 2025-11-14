package com.upc.ep.Controller;

import com.upc.ep.DTO.PedidoDTO;
import com.upc.ep.Entidades.Pedido;
import com.upc.ep.Services.PedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/pedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public PedidoDTO savePedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = modelMapper.map(pedidoDTO, Pedido.class);
        pedido = pedidoService.savePedido(pedido);
        return modelMapper.map(pedido, PedidoDTO.class);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/pedidos")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PedidoDTO> listarPedidos() {
        return pedidoService.listarPedidos().stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- EDITAR --------------------
    @PutMapping("/pedido/modificar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<Pedido> putPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido actualizado = pedidoService.putPedido(id, pedido);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // -------------------- ELIMINAR --------------------
    @DeleteMapping("/pedido/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarPedido(@PathVariable Long id) {
        boolean eliminado = pedidoService.eliminarPedido(id);
        if (eliminado) {
            return ResponseEntity.ok("Pedido eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        }
    }

    // -------------------- LISTAR POR ESTADO --------------------
    @GetMapping("/pedidos/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PedidoDTO> listarPorEstado(@PathVariable String estado) {
        return pedidoService.listarPorEstado(estado).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR FECHA --------------------
    @GetMapping("/pedidos/fecha/{fechaPedido}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PedidoDTO> listarPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPedido) {
        return pedidoService.listarPorFecha(fechaPedido).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR CLIENTE --------------------
    @GetMapping("/pedidos/cliente/{cliente}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PedidoDTO> listarPorCliente(@PathVariable String cliente) {
        return pedidoService.listarPorCliente(cliente).stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDTO.class))
                .collect(Collectors.toList());
    }
}
