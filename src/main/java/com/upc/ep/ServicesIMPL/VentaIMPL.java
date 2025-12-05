package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Detalle_Vent;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Repositorio.Detalle_VentRepos;
import com.upc.ep.Repositorio.TallaRepos;
import com.upc.ep.Repositorio.VentaRepos;
import com.upc.ep.Services.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VentaIMPL implements VentaService {
    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private Detalle_VentRepos detalleVentRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private PrendaIMPL prendaIMPL;


    @Override
    public Venta saveVenta(Venta venta) {
        // Fecha y hora automáticas al registrar
        venta.setFechahoraVenta(LocalDateTime.now());
        return ventaRepos.save(venta);
    }

    @Override
    public List<VentaDTO> listarVentas() {
        return ventaRepos.findAll().stream().map(v -> {
            VentaDTO dto = new VentaDTO();
            dto.setIdVenta(v.getIdVenta());
            dto.setCliente(v.getCliente());
            dto.setMetodoPago(v.getMetodoPago());
            dto.setFechahoraVenta(v.getFechahoraVenta());
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
        venta.setFechahoraVenta(LocalDateTime.now());

        Venta actualizada = ventaRepos.save(venta);

        VentaDTO dtoActualizado = new VentaDTO();
        dtoActualizado.setIdVenta(actualizada.getIdVenta());
        dtoActualizado.setCliente(actualizada.getCliente());
        dtoActualizado.setMetodoPago(actualizada.getMetodoPago());
        dtoActualizado.setFechahoraVenta(actualizada.getFechahoraVenta());

        return dtoActualizado;
    }

    @Override
    public List<Venta> listarPorMetodoPago(String metodoPago) {
        return ventaRepos.listarPorMetodoPago(metodoPago);
    }

    @Override
    public List<Venta> listarPorFecha(LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);
        return ventaRepos.listarPorRangoFechas(inicioDelDia, finDelDia);
    }

    @Override
    public List<Venta> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        LocalDateTime inicioDT = inicio.atStartOfDay();
        LocalDateTime finDT = fin.atTime(LocalTime.MAX);
        return ventaRepos.listarPorRangoFechas(inicioDT, finDT);
    }

    @Transactional
    public boolean eliminarVenta(Long id) {

        Venta venta = ventaRepos.findById(id).orElse(null);
        if (venta == null) return false;

        List<Detalle_Vent> detalles = detalleVentRepos.findByVentaIdVenta(id);

        // Guarda solo prendas únicas para recalculado
        Set<Prenda> prendasARecalcular = new HashSet<>();

        // 1. Restaurar stock de tallas
        for (Detalle_Vent det : detalles) {
            Talla talla = det.getTalla();

            talla.setCantidad(talla.getCantidad() + det.getCantidad());
            tallaRepos.save(talla);

            prendasARecalcular.add(talla.getPrenda());
        }

        // 2. Eliminar detalles
        detalleVentRepos.deleteByVentaIdVenta(id);

        // 3. Eliminar venta
        ventaRepos.deleteById(id);

        // 4. Recalcular estado + stock de cada prenda afectada
        for (Prenda p : prendasARecalcular) {
            prendaIMPL.restaurarStockYRecalcularEstado(p);
        }

        return true;
    }
}