package com.upc.ep.DTO;

import com.upc.ep.Entidades.Categoria;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModeloDTO implements Serializable {
    private Long idModelo;
    private String modelo;

    private CategoriaDTO categoria;
}
