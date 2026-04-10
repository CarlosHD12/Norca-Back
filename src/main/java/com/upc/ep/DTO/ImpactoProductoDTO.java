package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImpactoProductoDTO {
    private String categoria;
    private String marca;
    private String calidad;
    private String imagenCategoria;
    private Integer cantidadVendida;
    private Double ingreso;
    private Double costo;
    private Double ganancia;
    private List<ImpactoInventarioDTO> inventarios;
}
