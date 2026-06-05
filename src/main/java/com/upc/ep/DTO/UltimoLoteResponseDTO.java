package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltimoLoteResponseDTO {
    private Long idLote;
    private String codigoLote;
    private Integer cantidadInicial;
    private Integer stockActual;
    private BigDecimal precioCompraTotal;
    private BigDecimal precioVenta;
    private Boolean activo;
    private LocalDateTime fechaIngreso;
    private List<InventarioHistorialDTO> inventarios;
}
