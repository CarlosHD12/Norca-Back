package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.MarcaDTO;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Repositorio.MarcaRepos;
import com.upc.ep.Services.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarcaIMPL implements MarcaService {
    @Autowired
    private MarcaRepos marcaRepos;

    @Override
    public MarcaDTO registrarMarca(MarcaDTO marcaDTO) {
        Marca marca = new Marca();
        marca.setNombre(marcaDTO.getNombre());
        Marca nueva = marcaRepos.save(marca);

        marcaDTO.setIdMarca(nueva.getIdMarca());
        return marcaDTO;
    }

    @Override
    public List<MarcaDTO> listarMarcas() {
        return marcaRepos.findAllByOrderByIdMarcaDesc().stream()
                .map(m -> {
                    MarcaDTO dto = new MarcaDTO();
                    dto.setIdMarca(m.getIdMarca());
                    dto.setNombre(m.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public MarcaDTO actualizarMarca(Long id, MarcaDTO marcaDTO) {
        Marca marca = marcaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        marca.setNombre(marcaDTO.getNombre());
        marcaRepos.save(marca);
        marcaDTO.setIdMarca(id);
        return marcaDTO;
    }

    @Override
    public void eliminarMarca(Long id) {
        marcaRepos.deleteById(id);
    }
}