package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaOlvidadaDTO {
    private String categoria;
    private String marca;
    private String calidad;
    private Integer stock;
    private Double precioVenta;
}