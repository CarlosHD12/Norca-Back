package com.upc.ep.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaRegistroDTO {
    @NotNull(message = "El inventario es obligatorio")
    private Long inventarioId;

    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
