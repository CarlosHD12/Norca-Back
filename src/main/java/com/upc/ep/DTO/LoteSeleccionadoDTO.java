package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoteSeleccionadoDTO {
    private Long idLote;
    private String codigoLote;
    private BigDecimal precioVenta;
    private List<InventarioHistorialDTO> inventarios;
}
