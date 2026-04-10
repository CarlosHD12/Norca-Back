package com.upc.ep.Services;

import com.upc.ep.DTO.MarcaDTO;

import java.util.List;

public interface MarcaService {
    MarcaDTO registrarMarca(MarcaDTO marcaDTO);
    List<MarcaDTO> listarMarcas();
    MarcaDTO actualizarMarca(Long id, MarcaDTO marcaDTO);
    void eliminarMarca(Long id);
}
