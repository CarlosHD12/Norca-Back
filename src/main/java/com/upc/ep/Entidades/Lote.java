package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLote;

    @Column(nullable = false)
    private Integer numeroLote;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer stockActual;

    @Column(nullable = false)
    private Double precioCompraTotal;

    @Column(nullable = false)
    private Double precioVenta;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenda_idPrenda", nullable = false)
    private Prenda prenda;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inventario> inventarios = new HashSet<>();
}