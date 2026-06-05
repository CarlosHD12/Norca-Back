package com.upc.ep.Services;

import com.upc.ep.DTO.*;

import java.util.List;

public interface MarcaService {
    MarcaResponseDTO registrarMarca(MarcaRegistroDTO dto);
    List<MarcaResponseDTO> listarMarcas();
    MarcaResponseDTO editarMarca(Long idMarca, MarcaUpdateDTO dto);
    void desactivarMarca(Long idMarca);
    void activarMarca(Long idMarca);
}
