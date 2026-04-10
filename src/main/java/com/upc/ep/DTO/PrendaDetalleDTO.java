package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaDetalleDTO implements Serializable {
    private Long idPrenda;
    private String material;
    private LocalDate fechaRegistro;
    private String estado;
    private String descripcion;
    private List<String> colores;
    private String nombreCategoria;
    private String imagen;
    private String nombreMarca;
    private LoteDetalleDTO loteActivo;
}
