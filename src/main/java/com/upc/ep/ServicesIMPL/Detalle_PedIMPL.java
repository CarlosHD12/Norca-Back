package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Detalle_Ped;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.Detalle_PedRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Repositorio.TallaRepos;
import com.upc.ep.Services.Detalle_PedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Detalle_PedIMPL implements Detalle_PedService {
    @Autowired
    private Detalle_PedRepos detallePedRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    // -------------------- ACTUALIZA ESTADO DE LA PRENDA --------------------
    private void actualizarEstadoPrenda(Prenda prenda) {
        // Stock total de la prenda
        Integer stockTotal = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
        if (stockTotal == null) stockTotal = 0;

        // Revisar si tiene algún pedido pendiente
        boolean tienePedidoPendiente = detallePedRepos.existsByPrendaIdAndPedidoEstado(
                prenda.getIdPrenda(), "Pendiente"
        );

        if (tienePedidoPendiente) {
            prenda.setEstado("Pedido");
        } else if (stockTotal == 0) {
            prenda.setEstado("Agotado");
        } else {
            prenda.setEstado("Disponible");
        }

        prendaRepos.save(prenda);
    }

    // -------------------- GUARDAR DETALLE DE PEDIDO --------------------
    @Override
    public Detalle_Ped saveDetallePed(Detalle_Ped detalle) {
        // Validar prenda asociada
        if (detalle.getPrenda() == null || detalle.getPrenda().getIdPrenda() == null) {
            throw new RuntimeException("Debe asociar una prenda válida");
        }

        Prenda prenda = prendaRepos.findById(detalle.getPrenda().getIdPrenda())
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        // Asociar prenda al detalle
        detalle.setPrenda(prenda);

        // Guardar detalle en la base
        Detalle_Ped guardado = detallePedRepos.save(detalle);

        // Actualizar estado de la prenda según stock y pedidos pendientes
        actualizarEstadoPrenda(prenda);

        return guardado;
    }

    @Override
    public Detalle_Ped actualizarDetalle(Long id, Detalle_Ped detalleActualizado) {
        Detalle_Ped existente = detallePedRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        if (detalleActualizado.getPrenda() != null && detalleActualizado.getPrenda().getIdPrenda() != null) {
            Prenda prenda = prendaRepos.findById(detalleActualizado.getPrenda().getIdPrenda())
                    .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
            existente.setPrenda(prenda);
        }

        Detalle_Ped guardado = detallePedRepos.save(existente);

        // Actualizar estado de la prenda según stock y pedidos pendientes
        actualizarEstadoPrenda(guardado.getPrenda());

        return guardado;
    }

    @Override
    public List<Detalle_Ped> listarDetalles() {
        return detallePedRepos.findAll();
    }

    @Override
    public List<Detalle_Ped> listarPorPedido(Long idPedido) {
        return detallePedRepos.findByPedidoId(idPedido);
    }

    @Override
    public Integer contarPrendasPedido(Long idPedido) {
        return detallePedRepos.contarPrendasPorPedido(idPedido);
    }

    // -------------------- ELIMINAR DETALLE DE PEDIDO --------------------
    @Override
    public boolean eliminarDetalle(Long id) {
        Optional<Detalle_Ped> detalleOpt = detallePedRepos.findById(id);
        if (detalleOpt.isEmpty()) return false;

        Prenda prenda = detalleOpt.get().getPrenda();
        detallePedRepos.deleteById(id);

        // Actualizar estado de la prenda
        if (prenda != null) actualizarEstadoPrenda(prenda);

        return true;
    }
}
