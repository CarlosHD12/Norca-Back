package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Repositorio.Detalle_PedRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Repositorio.TallaRepos;
import com.upc.ep.Services.PrendaService;
import com.upc.ep.Services.TallaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TallaIMPL implements TallaService {
    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private Detalle_PedRepos dpRepos;

    @Autowired
    private PrendaService prendaService;

    @Override
    public List<Talla> listarTallas() {
        return tallaRepos.findAll();
    }

    @Override
    public List<Talla> listarPorPrenda(Long idPrenda) {
        return tallaRepos.findByPrendaId(idPrenda);
    }

    // Guardar tallas respetando el stock de la prenda
    @Override
    public Talla saveTalla(Talla talla) {
        if (talla.getPrenda() == null || talla.getPrenda().getIdPrenda() == null) {
            throw new RuntimeException("Debe asociar una prenda válida");
        }

        Prenda prenda = prendaRepos.findById(talla.getPrenda().getIdPrenda())
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        // Calcular stock disponible para asignar a tallas
        Integer stockUsado = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
        if (stockUsado == null) stockUsado = 0;
        int stockDisponible = prenda.getStock() - stockUsado;

        if (talla.getCantidad() > stockDisponible) {
            throw new RuntimeException("Cantidad de talla excede el stock disponible (" + stockDisponible + ")");
        }

        talla.setPrenda(prenda);
        Talla guardada = tallaRepos.save(talla);

        // Actualizar estado de la prenda
        prendaService.actualizarEstadoPrenda(prenda);

        return guardada;
    }

    // Editar talla también respetando stock
    @Override
    public Talla editarTalla(Long id, Talla tallaActualizada) {
        Talla talla = tallaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con ID: " + id));

        talla.setCantidad(tallaActualizada.getCantidad());
        talla.setSize(tallaActualizada.getSize());

        Talla actualizada = tallaRepos.save(talla);

        // Actualizar estado de la prenda después de editar tallas
        prendaService.actualizarEstadoPrenda(talla.getPrenda());

        return actualizada;
    }

    @Override
    public boolean eliminarTalla(Long id) {
        Optional<Talla> tallaOpt = tallaRepos.findById(id);
        if (tallaOpt.isEmpty()) return false;

        Prenda prenda = tallaOpt.get().getPrenda();

        tallaRepos.deleteById(id);

        // Actualizar estado de la prenda
        if (prenda != null) prendaService.actualizarEstadoPrenda(prenda);

        return true;
    }
}