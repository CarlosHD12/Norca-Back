package com.upc.ep.Entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrenda;

    private String talla;
    private String color;
    private String marca;
    private String calidad;
    private Double precioVenta;
    private String estado; //Disponible; Vendido; Pedido
    private Integer stock;
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "modelo_id")
    private Modelo modelo;

    @ManyToOne
    @JoinColumn(name = "detalle_ped_id", nullable = true)
    private Detalle_Ped detalle_ped;
}
