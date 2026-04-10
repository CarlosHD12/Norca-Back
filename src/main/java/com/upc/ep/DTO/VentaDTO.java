package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VentaDTO implements Serializable {
    private Long idVenta;
    private String codigo;
    private Integer unidades;
    private LocalDateTime fechaHora;
    private String metodoPago;
    private Double total;
    private Boolean estado;
    private List<Detalle_VentDTO> detalles;
}