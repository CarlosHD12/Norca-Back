package com.upc.ep.DTO;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioHistorialDTO {
    private Long idInventario;
    private String talla;
    private Integer stock;
}
