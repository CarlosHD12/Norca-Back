package com.upc.ep.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrendaRegistroDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String nombre;

    @NotBlank(message = "El material es obligatorio")
    @Size(max = 50)
    private String material;

    @Size(max = 255)
    private String descripcion;

    @Size(max = 500)
    private String imagenUrl;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La marca es obligatoria")
    private Long marcaId;

    private List<String> colores;
}