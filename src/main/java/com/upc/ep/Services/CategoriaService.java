package com.upc.ep.Services;

import com.upc.ep.DTO.CategoriaRegistroDTO;
import com.upc.ep.DTO.CategoriaResponseDTO;
import com.upc.ep.DTO.CategoriaUpdateDTO;
import com.upc.ep.Entidades.Categoria;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO registrarCategoria(CategoriaRegistroDTO dto);
    List<CategoriaResponseDTO> listarCategorias();
    CategoriaResponseDTO editarCategoria(Long idCategoria, CategoriaUpdateDTO dto);
    void desactivarCategoria(Long idCategoria);
    void activarCategoria(Long idCategoria);
}
