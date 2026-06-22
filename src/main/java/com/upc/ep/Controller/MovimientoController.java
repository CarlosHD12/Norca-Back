package com.upc.ep.Controller;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Services.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Norca")
public class MovimientoController {
    @Autowired
    private MovimientoService movimientoService;

    @PostMapping("/movimiento")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<MovimientoResponseDTO>
    registrarMovimiento(@Valid @RequestBody MovimientoRegistroDTO dto) {
        MovimientoResponseDTO response = movimientoService.registrarMovimiento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<MovimientoResponseDTO>> listarMovimientos(
            @RequestParam(required = false)
            Movimiento.ModuloMovimiento modulo,
            @RequestParam(required = false)
            Movimiento.TipoMovimiento tipoMovimiento,
            @RequestParam(required = false)
            String codigoReferencia,
            @RequestParam(required = false)
            Long usuarioId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                movimientoService.listarTodos(
                        modulo,
                        tipoMovimiento,
                        codigoReferencia,
                        usuarioId,
                        fechaInicio,
                        fechaFin,
                        pageable
                )
        );
    }

    @GetMapping("/modulo/{modulo}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MovimientoResponseDTO>> listarPorModulo(@PathVariable Movimiento.ModuloMovimiento modulo) {
        return ResponseEntity.ok(movimientoService.listarPorModulo(modulo)
        );
    }

    @GetMapping("/modulo/{modulo}/entidad/{entidadId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MovimientoResponseDTO>> listarPorEntidad(@PathVariable Movimiento.ModuloMovimiento modulo, @PathVariable Long entidadId) {
        return ResponseEntity.ok(movimientoService.listarPorEntidad(modulo, entidadId));
    }
}
