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
public class PrendaDTO implements Serializable {
    private Long idPrenda;
    private List<String> colores;
    private String calidad;
    private Integer stock;
    private Double precioCompra;
    private Double precioVenta;
    private String estado;
    private String descripcion;
    private LocalDate fechaRegistro;
    private MarcaDTO marca;
    private List<TallaSimpleDTO> tallas;
}
