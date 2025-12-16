package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaMetricasDTO {
    private Integer totalUnidadesVendidas;
    private Double ingresosTotales;
    private Integer cantidadVentas;
    private LocalDateTime fechaUltimaVenta;
    private Double gananciaAcumulada;
    private Double costoTotalInvertido;
    private Double tiempoPromedioDias;
    private Integer ranking;
}
