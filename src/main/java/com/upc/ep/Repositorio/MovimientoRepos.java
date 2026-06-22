package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepos extends JpaRepository<Movimiento, Long>, JpaSpecificationExecutor<Movimiento> {
    long countByTipoMovimientoAndFechaAfter(Movimiento.TipoMovimiento tipoMovimiento, LocalDateTime fecha);
    Page<Movimiento> findAllByOrderByFechaDesc(Pageable pageable);
    List<Movimiento> findByModuloAndEntidadIdOrderByFechaDesc(Movimiento.ModuloMovimiento modulo, Long entidadId);
    List<Movimiento> findByModuloAndEntidadIdInOrderByFechaDesc(Movimiento.ModuloMovimiento modulo, List<Long> entidadIds);
}
