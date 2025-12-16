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
    private Double precioUnitario;
    private Double subTotal; //cantidad * precioUnitario

    private PrendaDTO prenda;

    private VentaDTO venta;

    private TallaDTO talla;
}
