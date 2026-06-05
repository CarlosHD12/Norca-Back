package com.upc.ep.DTO;

import com.upc.ep.Entidades.Movimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRegistroDTO {
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private Movimiento.TipoMovimiento tipoMovimiento;

    @Size(max = 255)
    private String motivo;

    private String referenciaId;
}