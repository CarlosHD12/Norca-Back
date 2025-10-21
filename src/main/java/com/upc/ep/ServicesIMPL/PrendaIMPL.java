package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Services.PrendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrendaIMPL implements PrendaService {
    @Autowired
    private PrendaRepos prendaRepos;

    @Override
    public Prenda savePrenda(Prenda prenda) {
        // Fecha automática al registrar
        prenda.setFechaRegistro(LocalDate.now());

        // Estado por defecto
        prenda.setEstado("Disponible");
        return prendaRepos.save(prenda);
    }

    @Override
    public List<Prenda> listarPrendas() {
        return prendaRepos.findAll();
    }

    @Override
    public PrendaDTO putPrenda(Long id, PrendaDTO prendaDTO) {
        Prenda prenda = prendaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con ID: " + id));

        prenda.setTalla(prendaDTO.getTalla());
        prenda.setColor(prendaDTO.getColor());
        prenda.setMarca(prendaDTO.getMarca());
        prenda.setCalidad(prendaDTO.getCalidad());
        prenda.setPrecioVenta(prendaDTO.getPrecioVenta());
        prenda.setStock(prendaDTO.getStock());

        Prenda actualizada = prendaRepos.save(prenda);

        PrendaDTO dtoActualizado = new PrendaDTO();
        dtoActualizado.setIdPrenda(actualizada.getIdPrenda());
        dtoActualizado.setTalla(actualizada.getTalla());
        dtoActualizado.setColor(actualizada.getColor());
        dtoActualizado.setMarca(actualizada.getMarca());
        dtoActualizado.setCalidad(actualizada.getCalidad());
        dtoActualizado.setPrecioVenta(actualizada.getPrecioVenta());
        dtoActualizado.setStock(actualizada.getStock());
        dtoActualizado.setFechaRegistro(actualizada.getFechaRegistro());
        dtoActualizado.setEstado(actualizada.getEstado());

        return dtoActualizado;
    }
}
