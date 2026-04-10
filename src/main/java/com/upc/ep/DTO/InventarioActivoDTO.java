package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventarioActivoDTO implements Serializable {
    private Long idLote;
    private Integer stockLote;
    private Double precioVenta;
    private Long idInventario;
    private Integer stock;
    private String nombreTalla;
}