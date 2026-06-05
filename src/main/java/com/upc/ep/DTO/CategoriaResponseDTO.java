package com.upc.ep.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    private Long idCategoria;
    private String nombre;
    private Boolean activo;
}
