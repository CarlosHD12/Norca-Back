package com.upc.ep.Services;

import com.upc.ep.Entidades.Detalle_Ped;

import java.util.List;

public interface Detalle_PedService {
    Detalle_Ped saveDetallePed(Detalle_Ped detalle);
    Detalle_Ped actualizarDetalle(Long id, Detalle_Ped detalleActualizado);
    List<Detalle_Ped> listarDetalles();
    List<Detalle_Ped> listarPorPedido(Long idPedido);
    Integer contarPrendasPedido(Long idPedido);
    boolean eliminarDetalle(Long id);
}

