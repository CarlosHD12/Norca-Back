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
public class MarcaDTO implements Serializable {
    private Long idMarca;
    private String marca;

    private CategoriaDTO categoria;
}
