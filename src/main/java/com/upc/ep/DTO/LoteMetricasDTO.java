package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoteMetricasDTO implements Serializable {
    private Long idLote;
    private Double ventaTotal;
    private Double gananciaPorUnidad;
    private Double gananciaTotal;
    private Double margenGanancia;
    private Double radioInversion;
    private Double puntoEquilibrio;
}
