package com.upc.ep.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarcaResponseDTO {
    private Long idMarca;
    private String nombre;
    private Boolean activo;
}
