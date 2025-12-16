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
public class TallaDTO implements Serializable {
    private Long idTalla;
    private String size;  // S, M, L, XL, etc.
    private Integer cantidad; //cantidad por cada talla: 5 polos, 2: X , 3: X
    private PrendaDTO prenda;
}
