package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaListResponseDTO {
    private Long idVenta;
    private String codigo;
    private Integer unidades;
    private BigDecimal total;
    private String metodoPago;
    private String estadoVenta;
    private LocalDateTime fechaHora;
}
