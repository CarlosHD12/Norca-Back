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
public class VentaDTO implements Serializable {
    private Long idVenta;
    private String cliente;
    private String metodoPago;
    private LocalDateTime fechahoraVenta;
    private Double total;
}
