package com.upc.ep.Services;

import com.upc.ep.DTO.LoteDTO;

import java.util.List;

public interface LoteService {
    List<LoteDTO> obtenerLotesPorPrenda(Long prendaId);
}
