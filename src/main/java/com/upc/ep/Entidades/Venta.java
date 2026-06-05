package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
                @Index(name = "idx_venta_fecha", columnList = "fecha_hora"),
                @Index(name = "idx_venta_codigo", columnList = "codigo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idVenta")
public class Venta extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer unidades;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta", nullable = false)
    private EstadoVenta estadoVenta;

    @Column(nullable = false)
    private Boolean activo = true;

    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetalleVenta> detalleVentas = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }

        if (estadoVenta == null) {
            estadoVenta = EstadoVenta.COMPLETADA;
        }

        if (activo == null) {
            activo = true;
        }
    }

    public enum MetodoPago {
        EFECTIVO,
        YAPE,
        PLIN,
        TARJETA
    }

    public enum EstadoVenta {
        COMPLETADA,
        ANULADA
    }
}