package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO implements Serializable {
    private Long idPedido;
    private String cliente;
    private String descripcion;
    private LocalDateTime fechaPedido;
    private String estado;
}
