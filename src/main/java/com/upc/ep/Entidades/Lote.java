package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLote;

    private Integer cantidad;
    private Double precioCompraTotal;
    private LocalDate fechaIngreso = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "prenda_id", nullable = false)
    private Prenda prenda;
}