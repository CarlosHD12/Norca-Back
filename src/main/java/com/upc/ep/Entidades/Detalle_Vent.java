package com.upc.ep.Entidades;

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
public class Detalle_Vent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDV;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioVentaUnitario;

    @Column(nullable = false)
    private Double costoUnitario;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Integer stockAntes;

    @Column(nullable = false)
    private Integer stockDespues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_idVenta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_idInventario", nullable = false)
    private Inventario inventario;
}