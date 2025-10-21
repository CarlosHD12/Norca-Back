package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Detalle_Vent;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Repositorio.Detalle_VentRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Repositorio.VentaRepos;
import com.upc.ep.Services.Detalle_VentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Detalle_VentIMPL implements Detalle_VentService {
    @Autowired
    private Detalle_VentRepos dvRepos;

    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Override
    public Detalle_Vent saveDV(Detalle_Vent dv) {
        // Validar prenda
        Long idPrenda = dv.getPrenda().getIdPrenda();
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con ID: " + idPrenda));

        // Verificar stock suficiente
        if (prenda.getStock() < dv.getCantidad()) {
            throw new RuntimeException("No hay suficiente stock para la prenda: " + prenda.getIdPrenda());
        }

        // Calcular precio unitario y subtotal
        dv.setPrecioUnitario(prenda.getPrecioVenta());
        dv.setSubTotal(dv.getCantidad() * prenda.getPrecioVenta());

        // Descontar stock
        prenda.setStock(prenda.getStock() - dv.getCantidad());
        prendaRepos.save(prenda);

        // Guardar detalle
        dv.setPrenda(prenda);
        Detalle_Vent detalleGuardado = dvRepos.save(dv);

        // Actualizar total de la venta sin borrar otros datos
        if (detalleGuardado.getVenta() != null) {
            // Traer venta completa desde DB
            Venta venta = ventaRepos.findById(detalleGuardado.getVenta().getIdVenta())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            // Calcular nuevo total
            Double nuevoTotal = dvRepos.findAll().stream()
                    .filter(d -> d.getVenta().getIdVenta().equals(venta.getIdVenta()))
                    .mapToDouble(Detalle_Vent::getSubTotal)
                    .sum();

            venta.setTotal(nuevoTotal);

            // Guardar venta con total actualizado
            ventaRepos.save(venta);
        }

        return detalleGuardado;
    }


    @Override
    public List<Detalle_Vent> listarDV() {
        return dvRepos.findAll();
    }
}
