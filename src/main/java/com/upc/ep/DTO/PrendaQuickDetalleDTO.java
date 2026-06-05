package com.upc.ep.DTO;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrendaQuickDetalleDTO {
    private Long idPrenda;
    private String codigo;
    private String nombre;
    private String imagenUrl;
    private String estado;
    private Integer stockTotal;
    private Integer lotesActivos;
    private Long unidadesVendidasUltimos30Dias;
    private LoteFIFODTO loteFIFO;
}