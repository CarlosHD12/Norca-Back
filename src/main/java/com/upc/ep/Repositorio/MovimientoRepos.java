package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MovimientoRepos extends JpaRepository<Movimiento, Long> {
    long countByTipoMovimientoAndFechaAfter(Movimiento.TipoMovimiento tipoMovimiento, LocalDateTime fecha);
}
