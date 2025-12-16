package com.upc.ep.Services;

import com.upc.ep.Entidades.Pedido;

import java.time.LocalDate;
import java.util.List;

public interface PedidoService {
    public Pedido savePedido(Pedido pedido);
    public List<Pedido> listarPedidos();
    Pedido putPedido(Long id, Pedido pedidoActualizado);
    boolean eliminarPedido(Long id);
    List<Pedido> listarPorEstado(String estado);
    List<Pedido> listarPorFecha(LocalDate fechaPedido);
    List<Pedido> listarPorCliente(String cliente);
}
