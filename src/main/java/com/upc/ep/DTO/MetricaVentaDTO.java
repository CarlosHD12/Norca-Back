package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricaVentaDTO {
    private Long idMetrica;
    private Long idPrenda;
    private Integer unidadesVendidas;
    private BigDecimal ingresosTotales;
    private BigDecimal gananciaAcumulada;
    private BigDecimal inversionTotal;
    private Integer ventasRealizadas;
    private LocalDateTime ultimaVenta;
    private Double tiempoPromedioEntreVentas;
}
