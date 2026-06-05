package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_prenda_codigo", columnList = "codigo"),
                @Index(name = "idx_prenda_estado", columnList = "estado")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPrenda")
public class Prenda extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenda")
    private Long idPrenda;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 30, updatable = false)
    private String codigo;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String material;

    @Size(max = 255)
    @Column(length = 255)
    private String descripcion;

    @Size(max = 500)
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrenda estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private EstadoPrenda estadoAnterior;

    @Column(nullable = false)
    private Boolean activo = true;

    @ElementCollection
    @CollectionTable(name = "prenda_colores", joinColumns = @JoinColumn(name = "id_prenda"))
    @Column(name = "color", length = 30)
    private List<String> colores = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id_marca", nullable = false)
    private Marca marca;

    @OneToMany(mappedBy = "prenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Lote> lotes = new HashSet<>();

    @OneToOne(mappedBy = "prenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Metrica metrica;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPrenda.SIN_LOTES;
        }
        if (activo == null) {
            activo = true;
        }
    }

    public enum EstadoPrenda {
        SIN_LOTES,
        DISPONIBLE,
        AGOTADO,
        INHABILITADA
    }
}