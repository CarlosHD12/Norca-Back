package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "lote_id_lote",
                                "talla_id_talla"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_inventario_stock", columnList = "stock")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idInventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id_lote", nullable = false)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talla_id_talla", nullable = false)
    private Talla talla;

    @OneToMany(mappedBy = "inventario")
    @JsonIgnore
    private Set<DetalleVenta> detalleVentas = new HashSet<>();

    @Version
    private Long version;
}