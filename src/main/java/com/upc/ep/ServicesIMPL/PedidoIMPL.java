package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Detalle_Ped;
import com.upc.ep.Entidades.Pedido;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.Detalle_PedRepos;
import com.upc.ep.Repositorio.PedidoRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Repositorio.TallaRepos;
import com.upc.ep.Services.PedidoService;
import com.upc.ep.Services.PrendaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoIMPL implements PedidoService {
    @Autowired
    private PedidoRepos pedidoRepos;

    @Autowired
    private Detalle_PedRepos dpRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private PrendaService prendaService;


    @Override
    public Pedido savePedido(Pedido pedido) {
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("Pendiente"); // Estado por defecto
        return pedidoRepos.save(pedido);
    }

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepos.findAll();
    }

    @Override
    public Pedido putPedido(Long id, Pedido pedido) {
        Pedido pedidoExistente = pedidoRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        // Actualizar datos básicos
        pedidoExistente.setCliente(pedido.getCliente());
        pedidoExistente.setDescripcion(pedido.getDescripcion());
        pedidoExistente.setFechaPedido(pedido.getFechaPedido());
        pedidoExistente.setEstado(pedido.getEstado());

        Pedido pedidoActualizado = pedidoRepos.save(pedidoExistente);

        List<Detalle_Ped> detalles = dpRepos.findByPedidoId(pedidoActualizado.getIdPedido());

        for (Detalle_Ped dp : detalles) {
            Prenda prenda = dp.getPrenda();
            if (prenda == null) continue;

            String nuevoEstadoPedido = pedidoActualizado.getEstado();

            if ("Pendiente".equalsIgnoreCase(nuevoEstadoPedido)) {
                // Estado: Pendiente → prenda pasa a "Pedido"
                prenda.setEstado("Pedido");
                prendaRepos.save(prenda);

            } else if ("Completado".equalsIgnoreCase(nuevoEstadoPedido) ||
                    "Cancelado".equalsIgnoreCase(nuevoEstadoPedido)) {

                boolean enPedidoPendiente = dpRepos.existsByPrendaIdAndPedidoEstado(
                        prenda.getIdPrenda(), "Pendiente");

                if (enPedidoPendiente) {
                    // Todavía está en otro pedido pendiente → sigue en "Pedido"
                    prenda.setEstado("Pedido");
                } else {
                    Integer stockTotal = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
                    if (stockTotal == null) stockTotal = 0;
                    prenda.setStock(stockTotal);
                    prenda.setEstado(stockTotal > 0 ? "Disponible" : "Agotado");
                }

                prendaRepos.save(prenda);
            }
        }

        return pedidoActualizado;
    }

    @Override
    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepos.findByEstado(estado);
    }

    @Override
    public List<Pedido> listarPorFecha(LocalDate fechaPedido) {
        return pedidoRepos.findByFecha(fechaPedido);
    }

    @Override
    public List<Pedido> listarPorCliente(String cliente) {
        return pedidoRepos.findByClienteContaining(cliente);
    }

    @Override
    @Transactional
    public boolean eliminarPedido(Long id) {
        if (!pedidoRepos.existsById(id)) {
            return false;
        }

        // Obtener prendas asociadas
        List<Detalle_Ped> detalles = dpRepos.findByPedidoId(id);
        List<Long> idsPrendas = detalles.stream()
                .map(d -> d.getPrenda().getIdPrenda())
                .collect(Collectors.toList());

        // Eliminar detalles
        dpRepos.deleteByPedidoIdPedido(id);

        // Actualizar estado de cada prenda
        for (Long idPrenda : idsPrendas) {
            Prenda prenda = prendaRepos.findById(idPrenda)
                    .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
            prendaService.actualizarEstadoPrenda(prenda);
        }

        // Eliminar pedido
        pedidoRepos.deleteById(id);
        return true;
    }
}

