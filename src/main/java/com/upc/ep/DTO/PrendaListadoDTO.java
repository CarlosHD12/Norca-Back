package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrendaListadoDTO implements Serializable {
    private Long idPrenda;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private String material;
    private String descripcion;
    private String estado;
    private Integer stockActual;
    private Double precioVenta;
    private List<String> colores;
}
