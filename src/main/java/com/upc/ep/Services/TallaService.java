package com.upc.ep.Services;

import com.upc.ep.DTO.TallaDTO;

import java.util.List;

public interface TallaService {
    TallaDTO registrarTalla(TallaDTO tallaDTO);
    List<TallaDTO> listarTallas();
    TallaDTO actualizarTalla(Long id, TallaDTO tallaDTO);
    void eliminarTalla(Long id);
}
