package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.Detalle_VentDTO;
import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.DTO.TallaDTO;
import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Detalle_Vent;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.Detalle_VentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Detalle_VentIMPL implements Detalle_VentService {
    @Autowired
    private Detalle_VentRepos dvRepos;

    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private PrendaIMPL prendaIMPL;

    @Autowired
    private Detalle_PedRepos dpRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Detalle_VentRepos detalle_VentRepos;

    @Override
    @Transactional
    public Detalle_VentDTO saveDetalleVenta(Detalle_VentDTO dto) {
        Detalle_Vent detalle = new Detalle_Vent();

        // Datos simples
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setSubTotal(dto.getSubTotal());

        // Relación: Prenda
        Prenda prenda = null;
        if (dto.getPrenda() != null && dto.getPrenda().getIdPrenda() != null) {
            prenda = prendaRepos.findById(dto.getPrenda().getIdPrenda())
                    .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
            detalle.setPrenda(prenda);
        }

        // Relación: Venta
        Venta venta = null;
        if (dto.getVenta() != null && dto.getVenta().getIdVenta() != null) {
            venta = ventaRepos.findById(dto.getVenta().getIdVenta())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
            detalle.setVenta(venta);
        }

        // Relación: Talla (aquí se reduce el stock)
        if (dto.getTalla() != null && dto.getTalla().getIdTalla() != null) {
            Talla talla = tallaRepos.findById(dto.getTalla().getIdTalla())
                    .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

            // Validar stock disponible
            if (talla.getCantidad() < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para la talla seleccionada ("
                        + talla.getSize() + ")");
            }

            // Reducir stock
            talla.setCantidad(talla.getCantidad() - dto.getCantidad());
            tallaRepos.save(talla);

            detalle.setTalla(talla);

            // Actualizar estado de la prenda según stock total
            prendaIMPL.actualizarEstadoPrendaSegunPedidos(talla.getPrenda());
        }

        // Guardar detalle
        Detalle_Vent saved = dvRepos.save(detalle);

        // Recalcular total de la venta
        if (venta != null) {
            Double nuevoTotal = dvRepos.sumTotalByVenta(venta.getIdVenta());
            venta.setTotal(nuevoTotal != null ? nuevoTotal : 0.0);
            ventaRepos.save(venta);
        }

        // Convertir a DTO
        Detalle_VentDTO response = new Detalle_VentDTO();
        response.setIdDV(saved.getIdDV());
        response.setCantidad(saved.getCantidad());
        response.setPrecioUnitario(saved.getPrecioUnitario());
        response.setSubTotal(saved.getSubTotal());
        response.setPrenda(modelMapper.map(saved.getPrenda(), PrendaDTO.class));
        response.setVenta(modelMapper.map(saved.getVenta(), VentaDTO.class));
        response.setTalla(modelMapper.map(saved.getTalla(), TallaDTO.class));

        return response;
    }

        // -------------------- Función centralizada para actualizar estado de la prenda --------------------
    private void actualizarEstadoPrenda(Prenda prenda) {
        int stockTotal = prenda.getStock() != null ? prenda.getStock() : 0;

        boolean tienePedidoPendiente = dpRepos.existsByPrendaIdAndPedidoEstado(prenda.getIdPrenda(), "Pendiente");

        if (tienePedidoPendiente) {
            prenda.setEstado("Pedido");
        } else if (stockTotal == 0) {
            prenda.setEstado("Agotado");
        } else {
            prenda.setEstado("Disponible");
        }

        prendaRepos.save(prenda);
    }

    @Override
    public List<Detalle_VentDTO> listarDetalles() {
        return dvRepos.findAll()
                .stream()
                .map(d -> modelMapper.map(d, Detalle_VentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Detalle_VentDTO> listarPorPrenda(Long idPrenda) {
        return dvRepos.findByPrendaId(idPrenda)
                .stream()
                .map(d -> modelMapper.map(d, Detalle_VentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean eliminarDetalle(Long id) {
        Detalle_Vent detalle = detalle_VentRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        Talla talla = detalle.getTalla();

        // 🔄 Revertir stock vendido
        talla.setCantidad(talla.getCantidad() + detalle.getCantidad());
        tallaRepos.save(talla);

        // 🧩 Recalcular estado de prenda
        prendaIMPL.actualizarEstadoPrendaSegunPedidos(talla.getPrenda());

        detalle_VentRepos.delete(detalle);
        return true;
    }


    @Override
    @Transactional
    public Detalle_VentDTO actualizarDetalle(Long id, Detalle_VentDTO dto) {
        Detalle_Vent detalle = detalle_VentRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        Talla talla = detalle.getTalla();
        int diferencia = dto.getCantidad() - detalle.getCantidad();

        // Si aumenta la venta → restar más stock
        if (diferencia > 0) {
            if (talla.getCantidad() < diferencia) {
                throw new RuntimeException("Stock insuficiente para aumentar la venta");
            }
            talla.setCantidad(talla.getCantidad() - diferencia);
        }
        // Si disminuye la venta → devolver stock
        else if (diferencia < 0) {
            talla.setCantidad(talla.getCantidad() - diferencia); // negativo, así suma
        }

        tallaRepos.save(talla);

        detalle.setCantidad(dto.getCantidad());
        detalle_VentRepos.save(detalle);

        prendaIMPL.actualizarEstadoPrendaSegunPedidos(talla.getPrenda());

        return modelMapper.map(detalle, Detalle_VentDTO.class);
    }

    @Override
    public List<Detalle_Vent> listarPorVenta(Long idVenta) {
        return dvRepos.findByVentaIdVenta(idVenta);
    }

    @Override
    public Integer contarDetallesPorVenta(Long idVenta) {
        return dvRepos.countByVentaIdVenta(idVenta);
    }
}
