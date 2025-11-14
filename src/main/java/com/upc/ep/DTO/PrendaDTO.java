package com.upc.ep.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrendaDTO implements Serializable {
    private Long idPrenda;
    private String color; // Opcional
    private String calidad; //Obligatorio
    private Integer stock; //Obligatorio
    private Double precioCompra; //Obligatorio
    private Double precioVenta; //Obligatorio
    private String estado; //Disponible; Vendido; Pedido
    private String descripcion; //Opcional
    private LocalDate fechaRegistro; //Automatico

    private MarcaDTO marca;
    private List<TallaSimpleDTO> tallas;
}
