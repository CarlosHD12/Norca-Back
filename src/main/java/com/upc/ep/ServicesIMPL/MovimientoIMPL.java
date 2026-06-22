package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Repositorio.MovimientoRepos;
import com.upc.ep.Services.MovimientoService;
import com.upc.ep.Specification.MovimientoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoIMPL implements MovimientoService {
    @Autowired
    private MovimientoRepos movimientoRepos;

    @Override
    public MovimientoResponseDTO registrarMovimiento(MovimientoRegistroDTO dto) {
        Movimiento movimiento = new Movimiento();
        movimiento.setModulo(dto.getModulo());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setEntidadId(dto.getEntidadId());
        movimiento.setCodigoReferencia(dto.getCodigoReferencia());
        movimiento.setMotivo(dto.getMotivo());
        Movimiento guardado = movimientoRepos.save(movimiento);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoResponseDTO> listarTodos(
            Movimiento.ModuloMovimiento modulo,
            Movimiento.TipoMovimiento tipoMovimiento,
            String codigoReferencia,
            Long usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable
    ) {
        Specification<Movimiento> specification =
                MovimientoSpecification.filtrarMovimientos(
                        modulo,
                        tipoMovimiento,
                        codigoReferencia,
                        usuarioId,
                        fechaInicio,
                        fechaFin
                );
        return movimientoRepos
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> listarPorModulo(Movimiento.ModuloMovimiento modulo) {
        Specification<Movimiento> specification = MovimientoSpecification.filtrarMovimientos(
                        modulo,
                        null,
                        null,
                        null,
                        null,
                        null);
        return movimientoRepos.findAll(specification)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> listarPorEntidad(Movimiento.ModuloMovimiento modulo, Long entidadId) {
        return movimientoRepos
                .findByModuloAndEntidadIdOrderByFechaDesc(modulo, entidadId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MovimientoResponseDTO mapToResponse(Movimiento movimiento) {
        return MovimientoResponseDTO.builder()
                .idMovimiento(movimiento.getIdMovimiento())
                .modulo(movimiento.getModulo().name())
                .tipoMovimiento(movimiento.getTipoMovimiento().name())
                .entidadId(movimiento.getEntidadId())
                .codigoReferencia(movimiento.getCodigoReferencia())
                .motivo(movimiento.getMotivo())
                .usuario(movimiento.getCreadoPor())
                .fecha(movimiento.getFecha())
                .build();
    }
}
