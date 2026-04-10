package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false)
    private Integer unidades;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 50)
    private String metodoPago;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private Boolean estado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Detalle_Vent> detalle_Vents = new HashSet<>();
}