package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoteDetalleDTO implements Serializable {
    private Long idLote;
    private Integer numeroLote;
    private Integer cantidad;
    private Integer stockActual;
    private Double precioCompraTotal;
    private Double precioVenta;
    private LocalDate fechaIngreso;
    private Boolean activo;
    private List<HistorialDTO> historiales;
}
