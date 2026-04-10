package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.CategoriaDTO;
import com.upc.ep.Entidades.Categoria;
import com.upc.ep.Repositorio.CategoriaRepos;
import com.upc.ep.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaIMPL implements CategoriaService {
    @Autowired
    private CategoriaRepos categoriaRepos;

    @Override
    public CategoriaDTO registrarCategoria(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        Categoria nueva = categoriaRepos.save(categoria);

        categoriaDTO.setIdCategoria(nueva.getIdCategoria());
        return categoriaDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarCategorias() {
        return categoriaRepos.findAllByOrderByIdCategoriaDesc().stream()
                .map(m -> {
                    CategoriaDTO dto = new CategoriaDTO();
                    dto.setIdCategoria(m.getIdCategoria());
                    dto.setNombre(m.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setNombre(categoriaDTO.getNombre());
        categoriaRepos.save(categoria);
        categoriaDTO.setIdCategoria(id);
        return categoriaDTO;
    }

    @Override
    public void eliminarCategoria(Long id) {
        categoriaRepos.deleteById(id);
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    public void guardarImagen(Long id, MultipartFile file) {
        try {
            Categoria categoria = buscarPorId(id);
            categoria.setImagenBytes(file.getBytes());
            categoriaRepos.save(categoria);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar imagen");
        }
    }

    @Override
    public byte[] obtenerImagen(Long id) {
        Categoria categoria = buscarPorId(id);
        if (categoria.getImagenBytes() == null) {
            throw new RuntimeException("No hay imagen");
        }
        return categoria.getImagenBytes();
    }
}