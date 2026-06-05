package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentRepos extends JpaRepository<DetalleVenta, Long> {
}