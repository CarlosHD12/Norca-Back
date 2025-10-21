package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Repositorio.VentaRepos;
import com.upc.ep.Services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaIMPL implements VentaService {
    @Autowired
    private VentaRepos ventaRepos;

    @Override
    public Venta saveVenta(Venta venta) {
        // Fecha y hora automáticas al registrar
        venta.setFechaVenta(LocalDate.now());
        venta.setHoraVenta(LocalTime.now());
        return ventaRepos.save(venta);
    }

    @Override
    public List<VentaDTO> listarVentas() {
        return ventaRepos.findAll().stream().map(v -> {
            VentaDTO dto = new VentaDTO();
            dto.setIdVenta(v.getIdVenta());
            dto.setCliente(v.getCliente());
            dto.setMetodoPago(v.getMetodoPago());
            dto.setFechaVenta(v.getFechaVenta());
            dto.setHoraVenta(v.getHoraVenta());
            dto.setTotal(v.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public VentaDTO putVenta(Long id, VentaDTO ventaDTO) {
        Venta venta = ventaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        venta.setCliente(ventaDTO.getCliente());
        venta.setMetodoPago(ventaDTO.getMetodoPago());

        // Actualizamos automáticamente fecha y hora al modificar
        venta.setFechaVenta(LocalDate.now());
        venta.setHoraVenta(LocalTime.now());

        Venta actualizada = ventaRepos.save(venta);

        VentaDTO dtoActualizado = new VentaDTO();
        dtoActualizado.setIdVenta(actualizada.getIdVenta());
        dtoActualizado.setCliente(actualizada.getCliente());
        dtoActualizado.setMetodoPago(actualizada.getMetodoPago());
        dtoActualizado.setFechaVenta(actualizada.getFechaVenta());
        dtoActualizado.setHoraVenta(actualizada.getHoraVenta());

        return dtoActualizado;
    }
}
