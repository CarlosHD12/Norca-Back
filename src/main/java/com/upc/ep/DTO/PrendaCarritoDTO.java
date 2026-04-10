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
public class PrendaCarritoDTO implements Serializable {
    private Long idPrenda;
    private String categoria;
    private String imagen;
    private String marca;
    private String material;
    private String descripcion;
    private Double precioVenta;
    private Integer stock;
}
