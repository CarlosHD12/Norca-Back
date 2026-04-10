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
public class LotesTotalesDTO implements Serializable {
    private Long valorPrincipal;
    private Long subtitulo;
    private Double crecimiento;
}
