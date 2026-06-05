package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.MarcaRepos;
import com.upc.ep.Repositorio.MovimientoRepos;
import com.upc.ep.Services.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaIMPL implements MarcaService {
    @Autowired
    private MarcaRepos marcaRepos;

    @Autowired
    private MovimientoRepos movimientoRepos;

    @Override
    @Transactional
    public MarcaResponseDTO registrarMarca(MarcaRegistroDTO dto) {
        if (marcaRepos.existsByNombre(dto.getNombre())) {throw new RuntimeException("Ya existe una marca con ese nombre");}
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setActivo(true);
        Marca marcaGuardada = marcaRepos.save(marca);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_MARCA);
        movimiento.setMotivo("Se registró la marca: " + marcaGuardada.getNombre());
        movimiento.setReferenciaId("MAR-" + marcaGuardada.getIdMarca());
        movimientoRepos.save(movimiento);
        return mapToResponse(marcaGuardada);
    }

    private MarcaResponseDTO mapToResponse(Marca marca) {
        return MarcaResponseDTO.builder()
                .idMarca(marca.getIdMarca())
                .nombre(marca.getNombre())
                .activo(marca.getActivo())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarMarcas() {
        List<Marca> marcas = marcaRepos.findAllByOrderByIdMarcaDesc();
        return marcas.stream().map(this::toMarcaDTO).toList();
    }

    private MarcaResponseDTO toMarcaDTO(Marca marca) {
        return MarcaResponseDTO.builder()
                .idMarca(marca.getIdMarca())
                .nombre(marca.getNombre())
                .activo(marca.getActivo())
                .build();
    }

    @Override
    @Transactional
    public MarcaResponseDTO editarMarca(Long idMarca, MarcaUpdateDTO dto) {
        Marca marca = marcaRepos.findById(idMarca).orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        boolean existeNombre = marcaRepos.existsByNombreIgnoreCase(dto.getNombre());
        if (existeNombre && !marca.getNombre().equalsIgnoreCase(dto.getNombre())) {throw new RuntimeException("Ya existe una marca con ese nombre");}
        marca.setNombre(dto.getNombre());
        marcaRepos.save(marca);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.MODIFICACION_MARCA);
        movimiento.setMotivo("Se modificó la marca: " + marca.getNombre());
        movimiento.setReferenciaId("MAR-" + marca.getIdMarca().toString());
        movimientoRepos.save(movimiento);
        return toMarcaDTO(marca);
    }

    @Override
    @Transactional
    public void desactivarMarca(Long idMarca) {
        Marca marca = marcaRepos.findById(idMarca).orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        if (!marca.getActivo()) {throw new RuntimeException("La marca ya está desactivada");}
        boolean tienePrendasActivas =
                marca.getPrendas()
                        .stream()
                        .anyMatch(Prenda::getActivo);
        if (tienePrendasActivas) {throw new RuntimeException("No se puede desactivar una marca con prendas activas");}
        marca.setActivo(false);
        marcaRepos.save(marca);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.INHABILITACION_MARCA);
        movimiento.setMotivo("Se desactivó la marca: " + marca.getNombre());
        movimiento.setReferenciaId("MAR-" + marca.getIdMarca().toString());
        movimientoRepos.save(movimiento);
    }

    @Override
    @Transactional
    public void activarMarca(Long idMarca) {
        Marca marca = marcaRepos.findById(idMarca).orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        if (marca.getActivo()) {throw new RuntimeException("La marca ya está activa");}
        marca.setActivo(true);
        marcaRepos.save(marca);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_MARCA);
        movimiento.setMotivo("Se activó la marca: " + marca.getNombre());
        movimiento.setReferenciaId("MAR-" + marca.getIdMarca().toString());
        movimientoRepos.save(movimiento);
    }
}