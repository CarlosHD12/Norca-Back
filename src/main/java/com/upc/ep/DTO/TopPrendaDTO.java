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
public class TopPrendaDTO {
    private Long prendaId;
    private String calidad;
    private String marca;
    private String categoria;
    private Integer vendidos;
}
