package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteResponseDTO {
    private Long idLote;
    private String codigoLote;
    private Integer cantidadInicial;
    private Integer stockActual;
    private BigDecimal precioCompraTotal;
    private BigDecimal precioVenta;
    private LocalDateTime fechaIngreso;
    private Boolean activo;
    private String prendaCodigo;
}
