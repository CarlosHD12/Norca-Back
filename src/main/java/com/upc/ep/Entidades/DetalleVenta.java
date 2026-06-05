package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_detalle_venta", columnList = "venta_id_venta")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idDV")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dv")
    private Long idDV;

    @Positive
    @Column(nullable = false)
    private Integer cantidad;

    @Positive
    @Column(name = "precio_venta_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVentaUnitario;

    @Positive
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "stock_antes", nullable = false)
    private Integer stockAntes;

    @Column(name = "stock_despues", nullable = false)
    private Integer stockDespues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id_inventario", nullable = false)
    private Inventario inventario;

    @Version
    private Long version;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (precioVentaUnitario != null && cantidad != null) {
            subtotal = precioVentaUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}