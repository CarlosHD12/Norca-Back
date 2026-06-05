package com.upc.ep.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRegistroDTO {
    @NotNull(message = "La talla es obligatoria")
    private Long tallaId;

    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
}
