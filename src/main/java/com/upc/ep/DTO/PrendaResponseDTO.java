package com.upc.ep.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrendaResponseDTO {
    private Long idPrenda;
    private String codigo;
    private String nombre;
    private String material;
    private String descripcion;
    private String imagenUrl;
    private List<String> colores;
    private String categoria;
    private String marca;
    private String estado;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;
}