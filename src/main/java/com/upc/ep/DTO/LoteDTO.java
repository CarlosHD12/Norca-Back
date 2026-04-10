package com.upc.ep.DTO;

import jakarta.persistence.Column;
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
public class LoteDTO implements Serializable {
    private Long idLote;
    private Integer numeroLote;
    private Integer cantidad;
    private Integer stockActual;
    private Double precioCompraTotal;
    private Double precioVenta;
    private LocalDate fechaIngreso;
    private Boolean activo;
    private PrendaDTO prenda;
    private List<InventarioDTO> inventarios;
}