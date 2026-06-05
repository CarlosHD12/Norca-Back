package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_talla_nombre", columnList = "nombre")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTalla")
public class Talla extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talla")
    private Long idTalla;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, unique = true, length = 10)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "talla", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Inventario> inventarios = new HashSet<>();
}