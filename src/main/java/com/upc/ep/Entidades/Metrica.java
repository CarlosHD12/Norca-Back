package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_metrica_unidades", columnList = "unidades_vendidas")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idMetrica")
public class Metrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metrica")
    private Long idMetrica;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenda_id_prenda", nullable = false, unique = true)
    private Prenda prenda;

    @PositiveOrZero
    @Column(name = "unidades_vendidas", nullable = false)
    private Integer unidadesVendidas = 0;

    @PositiveOrZero
    @Column(name = "ingresos_totales", nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresosTotales = BigDecimal.ZERO;

    @Column(name = "ganancia_acumulada", nullable = false, precision = 12, scale = 2)
    private BigDecimal gananciaAcumulada = BigDecimal.ZERO;

    @Column(name = "inversion_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal inversionTotal = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "ventas_realizadas", nullable = false)
    private Integer ventasRealizadas = 0;

    @Column(name = "ultima_venta")
    private LocalDateTime ultimaVenta;

    @Column(name = "tiempo_promedio_entre_ventas")
    private Double tiempoPromedioEntreVentas;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        ultimaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }
}