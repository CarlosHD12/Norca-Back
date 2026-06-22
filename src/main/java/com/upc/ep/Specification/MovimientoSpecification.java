package com.upc.ep.Specification;

import com.upc.ep.Entidades.Movimiento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimientoSpecification {

    public static Specification<Movimiento> filtrarMovimientos(
            Movimiento.ModuloMovimiento modulo,
            Movimiento.TipoMovimiento tipoMovimiento,
            String codigoReferencia,
            Long usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {

        return (root, query, cb) -> {

            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();


            if (modulo != null) {

                predicates.add(
                        cb.equal(
                                root.get("modulo"),
                                modulo
                        )
                );
            }

            if (tipoMovimiento != null) {

                predicates.add(
                        cb.equal(
                                root.get("tipoMovimiento"),
                                tipoMovimiento
                        )
                );
            }

            if (codigoReferencia != null &&
                    !codigoReferencia.isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("codigoReferencia")),
                                "%" + codigoReferencia.toLowerCase() + "%"
                        )
                );
            }

            if (usuarioId != null) {

                predicates.add(
                        cb.equal(
                                root.get("creadoPor").get("id"),
                                usuarioId
                        )
                );
            }

            if (fechaInicio != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("fecha"),
                                fechaInicio
                        )
                );
            }

            if (fechaFin != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("fecha"),
                                fechaFin
                        )
                );
            }

            query.orderBy(
                    cb.desc(root.get("fecha"))
            );

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}