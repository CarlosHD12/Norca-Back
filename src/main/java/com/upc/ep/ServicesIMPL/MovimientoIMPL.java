package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.DTO.MovimientoResponseDTO;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Repositorio.MovimientoRepos;
import com.upc.ep.Services.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoIMPL implements MovimientoService {
    @Autowired
    private MovimientoRepos movimientoRepos;

    @Override
    @Transactional
    public MovimientoResponseDTO registrarMovimiento(MovimientoRegistroDTO dto) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setMotivo(dto.getMotivo());
        movimiento.setReferenciaId(dto.getReferenciaId());
        Movimiento movimientoGuardado = movimientoRepos.save(movimiento);
        return mapToResponse(movimientoGuardado);
    }

    private MovimientoResponseDTO mapToResponse(Movimiento movimiento) {
        return MovimientoResponseDTO
                .builder()
                .idMovimiento(movimiento.getIdMovimiento())
                .tipoMovimiento(movimiento.getTipoMovimiento().name())
                .motivo(movimiento.getMotivo())
                .referenciaId(movimiento.getReferenciaId())
                .fecha(movimiento.getFecha())
                .build();
    }

}
