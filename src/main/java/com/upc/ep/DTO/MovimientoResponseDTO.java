package com.upc.ep.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {
    private Long idMovimiento;
    private String tipoMovimiento;
    private String motivo;
    private String referenciaId;
    private LocalDateTime fecha;
}
