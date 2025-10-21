package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Modelo;
import com.upc.ep.Repositorio.ModeloRepos;
import com.upc.ep.Services.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloIMPL implements ModeloService {
    @Autowired
    private ModeloRepos modeloRepos;

    @Override
    public Modelo saveModelo(Modelo modelo) {
        return modeloRepos.save(modelo);
    }

    @Override
    public List<Modelo> listarModelos() {
        return modeloRepos.findAll();
    }
}
