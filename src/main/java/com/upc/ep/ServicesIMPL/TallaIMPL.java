package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.TallaDTO;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Repositorio.TallaRepos;

import com.upc.ep.Services.TallaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TallaIMPL implements TallaService {
    @Autowired
    private TallaRepos tallaRepos;

    @Override
    public TallaDTO registrarTalla(TallaDTO tallaDTO) {
        Talla talla = new Talla();
        talla.setNombre(tallaDTO.getNombre());
        Talla nueva = tallaRepos.save(talla);
        tallaDTO.setIdTalla(nueva.getIdTalla());
        return tallaDTO;
    }

    @Override
    public List<TallaDTO> listarTallas() {
        return tallaRepos.findAllByOrderByIdTallaDesc().stream()
                .map(m -> {
                    TallaDTO dto = new TallaDTO();
                    dto.setIdTalla(m.getIdTalla());
                    dto.setNombre(m.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TallaDTO actualizarTalla(Long id, TallaDTO TallaDTO) {
        Talla Talla = tallaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada"));
        Talla.setNombre(TallaDTO.getNombre());
        tallaRepos.save(Talla);
        TallaDTO.setIdTalla(id);
        return TallaDTO;
    }

    @Override
    public void eliminarTalla(Long id) {
        tallaRepos.deleteById(id);
    }
}