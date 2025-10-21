package com.upc.ep.Controller;

import com.upc.ep.DTO.PedidoDTO;
import com.upc.ep.Entidades.Pedido;
import com.upc.ep.Services.PedidoService;
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
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/pedido")
    public PedidoDTO savePedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = modelMapper.map(pedidoDTO, Pedido.class);
        pedido = pedidoService.savePedido(pedido);
        return modelMapper.map(pedido, PedidoDTO.class);
    }

    @GetMapping("/pedidos")
    public List<PedidoDTO> listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        return pedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDTO.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/pedido/modificar/{id}")
    public ResponseEntity<PedidoDTO> putPedido(@PathVariable Long id, @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO actualizado = pedidoService.putPedido(id, pedidoDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }
}
