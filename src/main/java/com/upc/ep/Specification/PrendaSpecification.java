package com.upc.ep.Specification;

import com.upc.ep.Entidades.Lote;
import com.upc.ep.Entidades.Prenda;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PrendaSpecification {

    public static Specification<Prenda> filtrarPrendas(
            String search,
            String categoria,
            String marca,
            String estado,
            Integer stockMin,
            Integer stockMax,
            BigDecimal precioMin,
            BigDecimal precioMax
    ) {

        return (root, query, cb) -> {

            // Evitar FETCH en consultas COUNT
            if (!Long.class.equals(query.getResultType())
                    && !long.class.equals(query.getResultType())) {

                root.fetch("categoria", JoinType.LEFT);
                root.fetch("marca", JoinType.LEFT);

                query.distinct(true);

                query.orderBy(
                        cb.desc(root.get("idPrenda"))
                );
            }

            List<Predicate> predicates = new ArrayList<>();

            // ELIMINADO:
            // predicates.add(cb.isTrue(root.get("activo")));

            if (search != null && !search.isBlank()) {

                String like = "%" + search.toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(
                                        cb.lower(root.get("nombre")),
                                        like
                                ),
                                cb.like(
                                        cb.lower(root.get("codigo")),
                                        like
                                )
                        )
                );
            }

            if (categoria != null && !categoria.isBlank()) {

                predicates.add(
                        cb.equal(
                                cb.lower(root.get("categoria").get("nombre")),
                                categoria.toLowerCase()
                        )
                );
            }

            if (marca != null && !marca.isBlank()) {

                predicates.add(
                        cb.equal(
                                cb.lower(root.get("marca").get("nombre")),
                                marca.toLowerCase()
                        )
                );
            }

            if (estado != null && !estado.isBlank()) {

                predicates.add(
                        cb.equal(
                                cb.lower(root.get("estado").as(String.class)),
                                estado.toLowerCase()
                        )
                );
            }

            Join<Prenda, Lote> loteJoin = null;

            boolean usarFiltrosLote =
                    stockMin != null ||
                            stockMax != null ||
                            precioMin != null ||
                            precioMax != null;

            if (usarFiltrosLote) {

                loteJoin = root.join("lotes", JoinType.LEFT);

                predicates.add(
                        cb.isTrue(loteJoin.get("activo"))
                );
            }

            if (stockMin != null && loteJoin != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                loteJoin.get("stockActual"),
                                stockMin
                        )
                );
            }

            if (stockMax != null && loteJoin != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                loteJoin.get("stockActual"),
                                stockMax
                        )
                );
            }

            if (precioMin != null && loteJoin != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                loteJoin.get("precioVenta"),
                                precioMin
                        )
                );
            }

            if (precioMax != null && loteJoin != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                loteJoin.get("precioVenta"),
                                precioMax
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}