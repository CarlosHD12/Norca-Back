package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepos extends JpaRepository<Pedido, Long> {
    //Lista Pedidos por estado
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado")
    List<Pedido> findByEstado(@Param("estado") String estado);

    //Lista Pedidos por fecha
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido = :fechaPedido")
    List<Pedido> findByFecha(@Param("fechaPedido") LocalDate fechaPedido);

    //Lista pedidos por clientes
    @Query("SELECT p FROM Pedido p WHERE p.cliente LIKE %:cliente%")
    List<Pedido> findByClienteContaining(@Param("cliente") String cliente);
}
