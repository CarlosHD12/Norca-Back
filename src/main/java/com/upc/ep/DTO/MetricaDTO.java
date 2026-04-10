package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetricaDTO implements Serializable {
    private Long idMetrica;
    private Long prendaId;
    private Integer unidadesVendidas;
    private Double ingresosTotales;
    private Double gananciaAcumulada;
    private Double inversionTotal;
    private Integer ventasRealizadas;
    private LocalDateTime ultimaVenta;
    private Double tiempoPromedioEntreVentas;
    private Double roi;
    private Integer ranking;
}
