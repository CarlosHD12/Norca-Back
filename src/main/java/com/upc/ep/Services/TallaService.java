package com.upc.ep.Services;

import com.upc.ep.DTO.*;

import java.util.List;

public interface TallaService {
    TallaResponseDTO registrarTalla(TallaRegistroDTO dto);
    List<TallaResponseDTO> listarTallas();
    TallaResponseDTO editarTalla(Long idTalla, TallaUpdateDTO dto);
    void desactivarTalla(Long idTalla);
    void activarTalla(Long idTalla);
}
