package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepos extends JpaRepository<Venta, Long> {
}
