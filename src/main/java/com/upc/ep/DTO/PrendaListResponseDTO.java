package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrendaListResponseDTO {
    private Long idPrenda;
    private Long categoriaId;
    private Long marcaId;
    private String codigo;
    private String imagenUrl;
    private String nombre;
    private String categoria;
    private String marca;
    private String material;
    private List<String> colores;
    private BigDecimal precioVenta;
    private Integer stock;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaRegistro;
}