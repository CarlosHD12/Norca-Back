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

    private Integer cantidad;
    private Double precioUnitario;
    private Double subTotal;

    @ManyToOne
    @JoinColumn(name = "prenda_id")
    private Prenda prenda;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;
}
