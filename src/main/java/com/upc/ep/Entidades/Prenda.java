package com.upc.ep.Entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrenda;

    @Column(nullable = false, length = 50)
    private String material;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false, length = 20)
    private String estado;

    @JsonIgnore
    private String estadoAnterior;

    @Column(length = 255)
    private String descripcion;

    @ElementCollection
    @CollectionTable(
            name = "prenda_colores",
            joinColumns = @JoinColumn(name = "idPrenda")
    )
    @Column(name = "color", nullable = false, length = 30)
    private List<String> colores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_idCategoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_idMarca", nullable = false)
    private Marca marca;

    @OneToMany(mappedBy = "prenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lote> lotes = new HashSet<>();

    @OneToOne(mappedBy = "prenda", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Metrica metrica;
}