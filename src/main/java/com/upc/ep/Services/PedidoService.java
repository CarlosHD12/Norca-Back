package com.upc.ep.Services;

import com.upc.ep.DTO.PedidoDTO;
import com.upc.ep.Entidades.Pedido;

import java.util.List;

public interface PedidoService {
    public Pedido savePedido(Pedido pedido);
    public List<Pedido> listarPedidos();
    PedidoDTO putPedido(Long id, PedidoDTO pedidoDTO);
}
