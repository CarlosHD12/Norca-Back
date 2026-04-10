package com.upc.ep.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class VentasMesDTO implements Serializable {
    private Integer mes;
    private Integer anio;
    private Long totalVentas;
    private Double ganancias;

    public VentasMesDTO(Integer mes, Integer anio, Long totalVentas, Double ganancias) {
        this.mes = mes;
        this.anio = anio;
        this.totalVentas = totalVentas;
        this.ganancias = ganancias;
    }
}