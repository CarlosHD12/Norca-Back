package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.PedidoDTO;
import com.upc.ep.Entidades.Pedido;
import com.upc.ep.Repositorio.PedidoRepos;
import com.upc.ep.Services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoIMPL implements PedidoService {
    @Autowired
    private PedidoRepos pedidoRepos;

    @Override
    public Pedido savePedido(Pedido pedido) {
        // Fecha automática al registrar
        pedido.setFechaPedido(LocalDate.now());

        // Estado por defecto al crear
        pedido.setEstado("Pendiente");

        return pedidoRepos.save(pedido);
    }

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepos.findAll();
    }

    @Override
    public PedidoDTO putPedido(Long id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        pedido.setCliente(pedidoDTO.getCliente());
        pedido.setDescripcion(pedidoDTO.getDescripcion());

        Pedido actualizado = pedidoRepos.save(pedido);

        PedidoDTO dtoActualizado = new PedidoDTO();
        dtoActualizado.setCliente(actualizado.getCliente());
        dtoActualizado.setDescripcion(actualizado.getDescripcion());
        dtoActualizado.setFechaPedido(actualizado.getFechaPedido());
        dtoActualizado.setEstado(actualizado.getEstado());

        return dtoActualizado;
    }
}

