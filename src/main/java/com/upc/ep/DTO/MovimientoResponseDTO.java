package com.upc.ep.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponseDTO {

    private Long idMovimiento;

    private String modulo;

    private String tipoMovimiento;

    private Long entidadId;

    private String codigoReferencia;

    private String motivo;

    private Long usuarioId;

    private String usuario;

    private LocalDateTime fecha;
}