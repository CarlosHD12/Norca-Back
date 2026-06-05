package com.upc.ep.Services;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;

public interface MovimientoService {
    MovimientoResponseDTO registrarMovimiento(MovimientoRegistroDTO dto);
}
