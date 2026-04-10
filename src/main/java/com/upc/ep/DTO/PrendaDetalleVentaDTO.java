package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaDetalleVentaDTO implements Serializable {
    private String categoria;
    private String imagen;
    private String marca;
    private String material;
    private String descripcion;
    private Double precioVentaUnitario;
    private Double totalPrenda;
    private List<TallaDetalleDTO> detallesTalla;
}