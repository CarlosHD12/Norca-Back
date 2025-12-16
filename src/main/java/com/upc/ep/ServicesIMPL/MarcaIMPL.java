package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Marca;
import com.upc.ep.Repositorio.MarcaRepos;
import com.upc.ep.Services.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcaIMPL implements MarcaService {
    @Autowired
    private MarcaRepos marcaRepos;

    @Override
    public Marca saveMarca(Marca marca) {
        return marcaRepos.save(marca);
    }

    @Override
    public List<Marca> listarMarcas() {
        return marcaRepos.findAllDistinct();
    }

    @Override
    public List<Marca> listarPorCategoria(Long idCategoria) {
        return marcaRepos.findMarcasByCategoria(idCategoria);
    }
}