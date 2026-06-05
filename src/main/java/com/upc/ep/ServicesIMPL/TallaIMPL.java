package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.TallaRegistroDTO;
import com.upc.ep.DTO.TallaResponseDTO;
import com.upc.ep.DTO.TallaUpdateDTO;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Repositorio.MovimientoRepos;
import com.upc.ep.Repositorio.TallaRepos;

import com.upc.ep.Services.TallaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TallaIMPL implements TallaService {
    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private MovimientoRepos movimientoRepos;

    @Override
    @Transactional
    public TallaResponseDTO registrarTalla(TallaRegistroDTO dto) {
        if (tallaRepos.existsByNombre(dto.getNombre())) {throw new RuntimeException("Ya existe una talla con ese nombre");}
        Talla talla = new Talla();
        talla.setNombre(dto.getNombre());
        talla.setActivo(true);
        Talla tallaGuardada = tallaRepos.save(talla);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_TALLA);
        movimiento.setMotivo("Se registró la talla: " + tallaGuardada.getNombre());
        movimiento.setReferenciaId("TAL-" + tallaGuardada.getIdTalla());
        movimientoRepos.save(movimiento);
        return mapToResponse(tallaGuardada);
    }

    private TallaResponseDTO mapToResponse(Talla talla) {
        return TallaResponseDTO.builder()
                .idTalla(talla.getIdTalla())
                .nombre(talla.getNombre())
                .activo(talla.getActivo())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TallaResponseDTO> listarTallas() {
        List<Talla> tallas = tallaRepos.findAllByOrderByIdTallaDesc();
        return tallas.stream()
                .map(this::toTallaDTO)
                .toList();
    }

    private TallaResponseDTO toTallaDTO(Talla talla) {
        return TallaResponseDTO.builder()
                .idTalla(talla.getIdTalla())
                .nombre(talla.getNombre())
                .activo(talla.getActivo())
                .build();
    }

    @Override
    @Transactional
    public TallaResponseDTO editarTalla(Long idTalla, TallaUpdateDTO dto) {
        Talla talla = tallaRepos.findById(idTalla).orElseThrow(() -> new RuntimeException("Talla no encontrada"));
        boolean existeNombre = tallaRepos.existsByNombreIgnoreCase(dto.getNombre());
        if (existeNombre && !talla.getNombre().equalsIgnoreCase(dto.getNombre())) {throw new RuntimeException("Ya existe una talla con ese nombre");}
        talla.setNombre(dto.getNombre());
        tallaRepos.save(talla);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.MODIFICACION_TALLA);
        movimiento.setMotivo("Se modificó la talla: " + talla.getNombre());
        movimiento.setReferenciaId("TAL-" + talla.getIdTalla().toString());
        movimientoRepos.save(movimiento);
        return toTallaDTO(talla);
    }

    @Override
    @Transactional
    public void desactivarTalla(Long idTalla) {
        Talla talla = tallaRepos.findById(idTalla).orElseThrow(() -> new RuntimeException("Talla no encontrada"));
        if (!talla.getActivo()) {throw new RuntimeException("La talla ya está desactivada");}
        boolean tieneInventario =
                talla.getInventarios()
                        .stream()
                        .anyMatch(inventario -> inventario.getStock() > 0);
        if (tieneInventario) {throw new RuntimeException("No se puede desactivar una talla con stock disponible");}
        talla.setActivo(false);
        tallaRepos.save(talla);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.INHABILITACION_TALLA);
        movimiento.setMotivo("Se desactivó la talla: " + talla.getNombre());
        movimiento.setReferenciaId("TAL-" + talla.getIdTalla().toString());
        movimientoRepos.save(movimiento);
    }

    @Override
    @Transactional
    public void activarTalla(Long idTalla) {
        Talla talla = tallaRepos.findById(idTalla).orElseThrow(() -> new RuntimeException("Talla no encontrada"));
        if (talla.getActivo()) {throw new RuntimeException("La talla ya está activa");}
        talla.setActivo(true);
        tallaRepos.save(talla);
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_TALLA);
        movimiento.setMotivo("Se activó la talla: " + talla.getNombre());
        movimiento.setReferenciaId("TAL-" + talla.getIdTalla().toString());
        movimientoRepos.save(movimiento);
    }
}