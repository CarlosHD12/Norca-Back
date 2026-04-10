package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Detalle_VentDTO implements Serializable {
    private Long idDV;
    private Integer cantidad;
    private Double precioVentaUnitario;
    private Double costoUnitario;
    private Double subtotal;
    private Integer stockAntes;
    private Integer stockDespues;
    private VentaDTO venta;
    private InventarioDTO inventario;
}