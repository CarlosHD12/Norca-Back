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
public class HistorialDTO implements Serializable {
    private Long idInventario;
    private TallaDTO talla;
    private Integer stock;
}
