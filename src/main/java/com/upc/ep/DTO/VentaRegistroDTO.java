package com.upc.ep.DTO;

import com.upc.ep.Entidades.Venta;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VentaRegistroDTO {
    @NotNull(message = "El método de pago es obligatorio")
    private Venta.MetodoPago metodoPago;

    @NotEmpty(message = "La venta debe tener detalles")
    private List<DetalleVentaRegistroDTO> detalles;
}
