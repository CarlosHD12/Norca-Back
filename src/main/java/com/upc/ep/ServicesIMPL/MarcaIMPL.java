package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.MarcaRepos;
import com.upc.ep.Services.MarcaService;
import com.upc.ep.Services.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaIMPL implements MarcaService {
    @Autowired
    private MarcaRepos marcaRepos;

    @Autowired
    private MovimientoService movimientoService;

    @Override
    @Transactional
    public MarcaResponseDTO registrarMarca(MarcaRegistroDTO dto) {
        if (marcaRepos.existsByNombre(dto.getNombre())) {throw new RuntimeException("Ya existe una marca con ese nombre");}
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setActivo(true);
        Marca marcaGuardada = marcaRepos.save(marca);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.MARCA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_MARCA)
                        .entidadId(marca.getIdMarca())
                        .codigoReferencia(marca.getNombre())
                        .motivo("Se registró la marca " + marca.getNombre())
                        .build()
        );
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
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.MARCA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.MODIFICACION_MARCA)
                        .entidadId(marca.getIdMarca())
                        .codigoReferencia(marca.getNombre())
                        .motivo("Se actualizó la marca " + marca.getNombre())
                        .build()
        );
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
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.MARCA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.INHABILITACION_MARCA)
                        .entidadId(marca.getIdMarca())
                        .codigoReferencia(marca.getNombre())
                        .motivo("Se inhabilitó la marca " + marca.getNombre())
                        .build()
        );
    }

    @Override
    @Transactional
    public void activarMarca(Long idMarca) {
        Marca marca = marcaRepos.findById(idMarca).orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        if (marca.getActivo()) {throw new RuntimeException("La marca ya está activa");}
        marca.setActivo(true);
        marcaRepos.save(marca);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.MARCA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_MARCA)
                        .entidadId(marca.getIdMarca())
                        .codigoReferencia(marca.getNombre())
                        .motivo("Se reactivó la marca " + marca.getNombre())
                        .build()
        );
    }
}