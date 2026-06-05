package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_movimiento_fecha", columnList = "fecha"),
                @Index(name = "idx_movimiento_tipo", columnList = "tipo_movimiento")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idMovimiento")
public class Movimiento extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private TipoMovimiento tipoMovimiento;

    @Size(max = 255)
    @Column(length = 255)
    private String motivo;

    @Column(name = "referencia_id", length = 50)
    private String referenciaId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public enum TipoMovimiento {
        // LOTES
        REGISTRO_LOTE,
        LOTE_AGOTADO,
        REACTIVACION_LOTE,
        CAMBIO_FIFO,


        // VENTAS / COMPLETO
        VENTA,
        ANULACION_VENTA,

        // PRENDAS / COMPLETO
        REGISTRO_PRENDA,
        MODIFICACION_PRENDA,
        CAMBIO_ESTADO_PRENDA,
        PRENDA_AGOTADA,
        INHABILITACION_PRENDA,
        REACTIVACION_PRENDA,

        // CATEGORÍAS / COMPLETO
        REGISTRO_CATEGORIA,
        MODIFICACION_CATEGORIA,
        INHABILITACION_CATEGORIA,
        REACTIVACION_CATEGORIA,

        // MARCAS / COMPLETO
        REGISTRO_MARCA,
        MODIFICACION_MARCA,
        INHABILITACION_MARCA,
        REACTIVACION_MARCA,

        // TALLAS / COMPLETO
        REGISTRO_TALLA,
        MODIFICACION_TALLA,
        INHABILITACION_TALLA,
        REACTIVACION_TALLA,

        // SISTEMA
        ACCION_ADMINISTRATIVA,
        CORRECCION_MANUAL
    }
}