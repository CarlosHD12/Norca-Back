package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrendaOlvidadaDTO implements Serializable {
    private Long idPrenda;
    private String categoria;
    private String marca;
    private String material;
    private String descripcion;
    private Integer stockActual;
    private LocalDate fechaUltimoLote;
    private LocalDateTime fechaUltimaVenta;
}
