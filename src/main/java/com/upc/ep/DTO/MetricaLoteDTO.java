package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricaLoteDTO {
    private BigDecimal ventaTotal;
    private BigDecimal gananciaPorUnidad;
    private BigDecimal gananciaTotal;
    private BigDecimal margenGanancia;
    private BigDecimal radioInversion;
    private Integer puntoEquilibrio;
}