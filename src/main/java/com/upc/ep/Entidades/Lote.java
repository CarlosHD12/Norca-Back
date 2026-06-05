package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_lote_fecha", columnList = "fecha_ingreso"),
                @Index(name = "idx_lote_codigo", columnList = "codigo_lote"),
                @Index(name = "idx_lote_activo", columnList = "activo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idLote")
public class Lote extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @NotBlank
    @Size(max = 50)
    @Column(name = "codigo_lote", nullable = false, unique = true, length = 50)
    private String codigoLote;

    @Positive
    @Column(nullable = false)
    private Integer cantidadInicial;

    @PositiveOrZero
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Positive
    @Column(name = "precio_compra_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompraTotal;

    @Positive
    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenda_id_prenda", nullable = false)
    private Prenda prenda;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventario> inventarios = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }

        if (stockActual == null) {
            stockActual = cantidadInicial;
        }

        if (activo == null) {
            activo = true;
        }
    }
}