package com.upc.ep.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteRegistroDTO {
    @Positive(message = "La cantidad inicial debe ser mayor a 0")
    private Integer cantidadInicial;

    @Positive(message = "El precio de compra debe ser mayor a 0")
    private BigDecimal precioCompraTotal;

    @Positive(message = "El precio de venta debe ser mayor a 0")
    private BigDecimal precioVenta;

    @NotNull(message = "La prenda es obligatoria")
    private Long prendaId;

    @NotEmpty(message = "Debe existir al menos un inventario")
    private List<InventarioRegistroDTO> inventarios;
}
