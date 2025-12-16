package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Categoria;
import com.upc.ep.Repositorio.CategoriaRepos;
import com.upc.ep.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaIMPL implements CategoriaService {
    @Autowired
    private CategoriaRepos categoriaRepos;

    @Override
    public Categoria saveCategoria(Categoria categoria) {
        return categoriaRepos.save(categoria);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepos.findAll();
    }
}