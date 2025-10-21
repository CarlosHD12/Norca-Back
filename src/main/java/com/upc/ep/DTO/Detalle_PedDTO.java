package com.upc.ep.DTO;

import com.upc.ep.Entidades.Pedido;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Detalle_PedDTO implements Serializable {
    private Long idDP;
    private Integer cantidad;

    private PedidoDTO pedido;
}
