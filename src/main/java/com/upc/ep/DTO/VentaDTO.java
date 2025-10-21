package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VentaDTO implements Serializable {
    private Long idVenta;
    private String cliente;
    private String metodoPago;
    private LocalDate fechaVenta;
    private LocalTime horaVenta;
    private Double total = 0.0;
}
