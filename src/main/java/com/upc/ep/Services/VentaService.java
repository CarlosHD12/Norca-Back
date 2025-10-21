package com.upc.ep.Services;

import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Venta;

import java.util.List;

public interface VentaService {
    public Venta saveVenta(Venta venta);
    public List<VentaDTO> listarVentas();
    VentaDTO putVenta(Long id, VentaDTO ventaDTO);
}
