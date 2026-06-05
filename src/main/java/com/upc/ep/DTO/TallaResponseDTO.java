package com.upc.ep.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TallaResponseDTO {
    private Long idTalla;
    private String nombre;
    private Boolean activo;
}
