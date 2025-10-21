package com.upc.ep.Services;

import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.Entidades.Prenda;

import java.util.List;

public interface PrendaService {
    public Prenda savePrenda(Prenda prenda);
    public List<Prenda> listarPrendas();
    PrendaDTO putPrenda(Long id, PrendaDTO prendaDTO);
}
