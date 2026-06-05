package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaKpiResponseDTO {
    private Long totalPrendas;
    private Long prendasUltimoMes;
    private Long prendasDisponibles;
    private Long disponiblesUltimaSemana;
    private Long prendasAgotadas;
    private Long agotadasUltimaSemana;
    private Long lotesActivos;
    private Long lotesUltimoMes;
    private BigDecimal valorTotalInventario;
    private BigDecimal inversionUltimoMes;}
