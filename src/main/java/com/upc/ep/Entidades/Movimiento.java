package com.upc.ep.Entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_movimiento_fecha", columnList = "fecha"),
                @Index(name = "idx_movimiento_tipo", columnList = "tipo_movimiento"),
                @Index(name = "idx_movimiento_modulo", columnList = "modulo")
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
    @Column(nullable = false, length = 30)
    private ModuloMovimiento modulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private TipoMovimiento tipoMovimiento;

    @Size(max = 255)
    @Column(length = 255)
    private String motivo;

    @Column(nullable = false)
    private Long entidadId;

    @Column(nullable = false)
    private String codigoReferencia;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public enum ModuloMovimiento {
        PRENDA,
        LOTE,
        VENTA,
        CATEGORIA,
        MARCA,
        TALLA,
        USUARIO,
        SISTEMA
    }

    public enum TipoMovimiento {
        // PRENDAS
        REGISTRO_PRENDA,
        MODIFICACION_PRENDA,
        INHABILITACION_PRENDA,
        REACTIVACION_PRENDA,
        PRENDA_DISPONIBLE,
        PRENDA_AGOTADA,

        // LOTES
        REGISTRO_LOTE,
        LOTE_AGOTADO,
        REACTIVACION_LOTE,

        // VENTAS
        REGISTRO_VENTA,
        ANULACION_VENTA,

        // CATEGORIAS
        REGISTRO_CATEGORIA,
        MODIFICACION_CATEGORIA,
        INHABILITACION_CATEGORIA,
        REACTIVACION_CATEGORIA,

        // MARCAS
        REGISTRO_MARCA,
        MODIFICACION_MARCA,
        INHABILITACION_MARCA,
        REACTIVACION_MARCA,

        // TALLAS
        REGISTRO_TALLA,
        MODIFICACION_TALLA,
        INHABILITACION_TALLA,
        REACTIVACION_TALLA,

        // USUARIOS
        REGISTRO_USUARIO,
        CAMBIO_ROL_USUARIO,
        INHABILITACION_USUARIO,
        REACTIVACION_USUARIO,

        // SISTEMA
        ACCION_ADMINISTRATIVA
    }
}