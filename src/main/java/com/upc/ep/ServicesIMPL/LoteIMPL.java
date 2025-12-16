package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.LoteDTO;
import com.upc.ep.Entidades.Lote;
import com.upc.ep.Repositorio.LoteRepos;
import com.upc.ep.Services.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteIMPL implements LoteService {
    @Autowired
    private LoteRepos loteRepos;

    @Override
    public List<LoteDTO> obtenerLotesPorPrenda(Long prendaId) {
        List<Lote> lotes = loteRepos.findByPrendaIdOrderByFechaIngresoDesc(prendaId);
        return lotes.stream()
                .map(this::convertirALoteDTO)
                .collect(Collectors.toList());
    }

    private LoteDTO convertirALoteDTO(Lote lote) {
        LoteDTO dto = new LoteDTO();
        dto.setIdLote(lote.getIdLote());
        dto.setCantidad(lote.getCantidad());
        dto.setPrecioCompraTotal(lote.getPrecioCompraTotal());
        dto.setFechaIngreso(lote.getFechaIngreso());

        dto.setPrecioCompraUnitario(
                (lote.getCantidad() != null && lote.getCantidad() > 0 && lote.getPrecioCompraTotal() != null)
                        ? lote.getPrecioCompraTotal() / lote.getCantidad()
                        : 0.0
        );

        return dto;
    }
}
