package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteFIFODTO {
    private Long idLote;
    private String codigoLote;
    private Boolean activo;
    private LocalDateTime fechaIngreso;
    private Integer cantidadInicial;
    private Integer stockActual;
    private BigDecimal precioVenta;
    private List<InventarioHistorialDTO> inventarios;
}