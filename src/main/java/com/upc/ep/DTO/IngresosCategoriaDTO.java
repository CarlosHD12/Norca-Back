package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngresosCategoriaDTO implements Serializable {
    private String categoria;
    private Double subtotalTotal;
    private Long cantidadTotal;
}