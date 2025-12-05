package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoteDTO {
    private Long idLote;
    private Integer cantidad;
    private Double precioCompraTotal;
    private Double precioCompraUnitario;
    private LocalDate fechaIngreso = LocalDate.now();
}