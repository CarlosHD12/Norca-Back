package com.upc.ep.Services;

import com.upc.ep.Entidades.Modelo;

import java.util.List;

public interface ModeloService {
    public Modelo saveModelo(Modelo modelo);
    public List<Modelo> listarModelos();
}
