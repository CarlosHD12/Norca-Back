package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaDTO implements Serializable {
    private Long idPrenda;
    private String talla;
    private String color;
    private String marca;
    private String calidad;
    private Double precioVenta;
    private String estado; //Disponible; Vendido; Pedido
    private Integer stock;
    private LocalDate fechaRegistro;

    private ModeloDTO modelo;

    private Detalle_PedDTO detalle_ped;
}
