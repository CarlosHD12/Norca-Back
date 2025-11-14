package com.upc.ep.Services;

import com.upc.ep.Entidades.Talla;

import java.util.List;

public interface TallaService {
    public Talla saveTalla(Talla talla);
    public List<Talla> listarTallas();
    List<Talla> listarPorPrenda(Long idPrenda);
    Talla editarTalla(Long id, Talla tallaActualizada);
    boolean eliminarTalla(Long id);
}
