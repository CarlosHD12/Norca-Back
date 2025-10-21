package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepos extends JpaRepository<Pedido, Long> {
}
