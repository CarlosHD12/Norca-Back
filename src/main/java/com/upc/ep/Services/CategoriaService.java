package com.upc.ep.Services;

import com.upc.ep.DTO.CategoriaDTO;
import com.upc.ep.Entidades.Categoria;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoriaService {
    CategoriaDTO registrarCategoria(CategoriaDTO categoriaDTO);
    List<CategoriaDTO> listarCategorias();
    CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO);
    void eliminarCategoria(Long id);
    Categoria buscarPorId(Long id);
    void guardarImagen(Long id, MultipartFile file);
    byte[] obtenerImagen(Long id);
}
