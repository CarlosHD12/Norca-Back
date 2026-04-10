package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImpactoVentaDTO {
    private String codigoVenta;
    private LocalDateTime fecha;
    private Double ingresoTotal;
    private Double costoTotal;
    private Double ganancia;
    private Double margen;
    private List<ImpactoProductoDTO> productos;
    private String analisis;
}