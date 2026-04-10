package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImpactoInventarioDTO {
    private String talla;
    private Integer stockAntes;
    private Integer stockDespues;
}
