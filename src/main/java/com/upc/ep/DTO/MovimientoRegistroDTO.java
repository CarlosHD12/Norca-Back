package com.upc.ep.DTO;

import com.upc.ep.Entidades.Movimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoRegistroDTO {
    @NotNull
    private Movimiento.ModuloMovimiento modulo;

    @NotNull
    private Movimiento.TipoMovimiento tipoMovimiento;

    @NotNull
    private Long entidadId;

    @NotBlank
    private String codigoReferencia;

    @Size(max = 255)
    private String motivo;
}