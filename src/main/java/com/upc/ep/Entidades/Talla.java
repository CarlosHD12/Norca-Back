package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Talla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTalla;

    @Column(nullable = false, length = 10, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "talla", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventario> inventarios = new HashSet<>();
}