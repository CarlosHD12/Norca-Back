package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepos extends JpaRepository<Venta, Long> {
    //Listar por metodo de pago
    @Query("SELECT v FROM Venta v WHERE LOWER(v.metodoPago) = LOWER(:metodoPago)")
    List<Venta> listarPorMetodoPago(@Param("metodoPago") String metodoPago);

    // Listar por fecha exacta (comparando solo la parte de la fecha)
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechahoraVenta) = :fecha")
    List<Venta> listarPorFecha(@Param("fecha") LocalDate fecha);

    // Listar por rango de fechas (usando LocalDateTime)
    @Query("SELECT v FROM Venta v WHERE v.fechahoraVenta BETWEEN :inicio AND :fin")
    List<Venta> listarPorRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
