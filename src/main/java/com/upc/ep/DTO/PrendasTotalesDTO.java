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
public class PrendasTotalesDTO implements Serializable {
    private Long totalPrendas;
    private Long prendasUltimoMes;
    private Double crecimiento;
}
