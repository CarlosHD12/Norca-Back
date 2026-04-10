package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO implements Serializable {
    private Long idInventario;
    private LoteDTO lote;
    private TallaDTO talla;
    private Integer stock;
}
