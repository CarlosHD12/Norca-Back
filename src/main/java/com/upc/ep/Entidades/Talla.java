package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Talla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTalla;

    private String size;    // S, M, L, XL, etc.
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenda_id")
    @JsonIgnoreProperties({"tallas", "marca"}) // 👈 ignora esas propiedades del otro lado
    private Prenda prenda;
}
