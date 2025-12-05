package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaStockBajoDTO {
    private Long prendaId;
    private String categoria;
    private String marca;
    private String calidad;
    private Integer stock;
    private Double precioVenta;
}
