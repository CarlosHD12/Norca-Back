package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumenLoteDTO {
    private Integer antiguedadDias;
    private String estadoComercial;
    private BigDecimal valorInventario;
    private BigDecimal gananciaPotencial;
}
