package com.upc.ep.DTO;

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

    private PedidoDTO pedido;

    private PrendaDTO prenda;
}
