package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Metrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMetrica;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenda_idPrenda", nullable = false)
    private Prenda prenda;

    private Integer unidadesVendidas = 0;
    private Double ingresosTotales = 0.0;
    private Double gananciaAcumulada = 0.0;
    private Double inversionTotal = 0.0;
    private Integer ventasRealizadas = 0;
    private LocalDateTime ultimaVenta;
    private Double tiempoPromedioEntreVentas;
    private Double roi;
    private Integer ranking;
}