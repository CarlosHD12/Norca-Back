package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Prenda;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrendaRepos extends JpaRepository<Prenda, Long> {
    // Trae la prenda con marca y tallas cargadas para que el front pueda usarlas
    @Query("SELECT p FROM Prenda p " +
            "LEFT JOIN FETCH p.marca " +
            "LEFT JOIN FETCH p.tallas " +
            "WHERE p.idPrenda = :id")
    Optional<Prenda> findByIdWithDetails(@Param("id") Long id);

    //Obtener Prenda por marca
    @Query("SELECT p FROM Prenda p JOIN FETCH p.marca m WHERE m.idMarca = :idMarca")
    List<Prenda> listarPorMarca(@Param("idMarca") Long idMarca);

    //Listar prendas por categorias
    @Query("SELECT p FROM Prenda p WHERE p.marca.categoria.idCategoria = :idCategoria")
    List<Prenda> listarPorCategoria(@Param("idCategoria") Long idCategoria);

    //Listar prendas por calidad
    @Query("SELECT p FROM Prenda p WHERE p.calidad = :calidad")
    List<Prenda> listarPorCalidad(@Param("calidad") String calidad);

    //Listar prendas por estado
    @Query("SELECT p FROM Prenda p WHERE p.estado = :estado")
    List<Prenda> listarPorEstado(@Param("estado") String estado);

    //Listar todas las marcas distintas
    @Query("SELECT DISTINCT p.marca FROM Prenda p ORDER BY p.marca.marca ASC")
    List<Marca> listarMarcas();

    //Buscar por rango de precio
    List<Prenda> findByPrecioVentaBetween(Double min, Double max);

    //Buscar por fecha exacta de registro
    List<Prenda> findByFechaRegistro(LocalDate fechaRegistro);

    // Actualizar solo estado de una prenda
    @Modifying
    @Transactional
    @Query("UPDATE Prenda p SET p.estado = :nuevoEstado WHERE p.idPrenda = :id")
    int actualizarEstado(@Param("id") Long id, @Param("nuevoEstado") String nuevoEstado);

    boolean existsByMarca_IdMarcaAndCalidad(Long idMarca, String calidad);

    @Query("SELECT p FROM Prenda p " +
            "LEFT JOIN p.marca m " +
            "LEFT JOIN m.categoria c " +
            "WHERE (:descripcion IS NULL OR p.descripcion LIKE %:descripcion%) " +
            "AND (:idMarca IS NULL OR m.idMarca = :idMarca) " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria) " +
            "AND (:estado IS NULL OR p.estado = :estado) " +
            "AND (:fecha IS NULL OR p.fechaRegistro = :fecha) " +
            "AND ((:fechaDesde IS NULL OR :fechaHasta IS NULL) OR (p.fechaRegistro BETWEEN :fechaDesde AND :fechaHasta))")
    List<Prenda> buscarPrendas(
            @Param("descripcion") String descripcion,
            @Param("idMarca") Long idMarca,
            @Param("idCategoria") Long idCategoria,
            @Param("estado") String estado,
            @Param("fecha") LocalDate fecha,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );

}
