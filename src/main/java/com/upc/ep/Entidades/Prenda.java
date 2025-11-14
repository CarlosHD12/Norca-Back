package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrenda;

    private String color; // Opcional
    private String calidad; //Obligatorio
    private Integer stock;
    private Double precioCompra; //Obligatorio
    private Double precioVenta; //Obligatorio
    private String estado; //Disponible; Vendido; Pedido
    private String descripcion; //Opcional
    private LocalDate fechaRegistro; //Automatico

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @OneToMany(mappedBy = "prenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("prenda")
    private List<Talla> tallas = new ArrayList<>();
}
