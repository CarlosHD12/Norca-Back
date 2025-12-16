package com.upc.ep.Services;

import com.upc.ep.Entidades.Marca;

import java.util.List;

public interface MarcaService {
    public Marca saveMarca(Marca marca);
    public List<Marca> listarMarcas();
    List<Marca> listarPorCategoria(Long idCategoria);
}
