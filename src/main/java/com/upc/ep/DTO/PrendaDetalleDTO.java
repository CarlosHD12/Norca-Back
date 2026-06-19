package com.upc.ep.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrendaDetalleDTO {
    private Long idPrenda;
    private String codigo;
    private String nombre;
    private String imagenUrl;
    private String categoria;
    private String marca;
    private String estado;
    private LocalDateTime fechaRegistro;
    private String material;
    private String descripcion;
    private List<String> colores;

    // ÚLTIMO LOTE REGISTRADO
    private Long idLote;
    private String codigoLote;
    private Integer cantidadInicial;
    private Integer stockActual;
    private BigDecimal precioVenta;
    private BigDecimal precioCompra;
    private LocalDateTime fechaIngreso;

    // INVENTARIOS
    private List<InventarioHistorialDTO> inventarios;

    // RESUMEN DEL LOTE
    private ResumenLoteDTO resumen;
}