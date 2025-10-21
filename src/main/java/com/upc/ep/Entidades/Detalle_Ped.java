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
public class Detalle_Ped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDP;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}
