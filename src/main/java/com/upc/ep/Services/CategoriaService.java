package com.upc.ep.Services;

import com.upc.ep.Entidades.Categoria;

import java.util.List;

public interface CategoriaService {
    public Categoria saveCategoria(Categoria categoria);
    public List<Categoria> listarCategorias();
}
