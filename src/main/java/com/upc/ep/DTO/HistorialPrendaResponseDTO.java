package com.upc.ep.DTO;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPrendaResponseDTO {
    private Long idPrenda;
    private String codigoPrenda;
    private List<LoteHistorialResponseDTO> lotes;
}
