package com.upc.ep.Services;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;
import com.upc.ep.Entidades.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {
    MovimientoResponseDTO registrarMovimiento(MovimientoRegistroDTO dto);
    Page<MovimientoResponseDTO> listarTodos(
            Movimiento.ModuloMovimiento modulo,
            Movimiento.TipoMovimiento tipoMovimiento,
            String codigoReferencia,
            Long usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable
    );
    List<MovimientoResponseDTO> listarPorModulo(Movimiento.ModuloMovimiento modulo);
    List<MovimientoResponseDTO> listarPorEntidad(Movimiento.ModuloMovimiento modulo, Long entidadId);
}
