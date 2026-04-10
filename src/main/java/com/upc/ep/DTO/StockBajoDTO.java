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
public class StockBajoDTO implements Serializable {
    private Long idPrenda;
    private String categoria;
    private String marca;
    private String material;
    private String descripcion;
    private Long idLote;
    private Integer cantidadInicial;
    private Long stockRestante;
}
