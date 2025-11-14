package com.upc.ep.Repositorio;

import com.upc.ep.Entidades.Detalle_Ped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Detalle_PedRepos extends JpaRepository<Detalle_Ped, Long> {
    //Listar pedido por Id
    @Query("SELECT d FROM Detalle_Ped d WHERE d.pedido.idPedido = :idPedido")
    List<Detalle_Ped> findByPedidoId(@Param("idPedido") Long idPedido);

    //Cantidad de prendas por pedido
    @Query("SELECT COUNT(d) FROM Detalle_Ped d WHERE d.pedido.idPedido = :idPedido")
    Integer contarPrendasPorPedido(@Param("idPedido") Long idPedido);

    //Verifica si existe una prenda en estado pedido
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Detalle_Ped d " +
            "WHERE d.prenda.idPrenda = :idPrenda AND d.pedido.estado = :estado")
    boolean existsByPrendaIdAndPedidoEstado(@Param("idPrenda") Long idPrenda,
                                            @Param("estado") String estado);

    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN true ELSE false END " +
            "FROM Detalle_Ped dp WHERE dp.prenda.idPrenda = :idPrenda AND dp.pedido.estado = 'Pendiente'")

    boolean existePedidoPendientePorPrenda(@Param("idPrenda") Long idPrenda);
    boolean existsByPrenda_IdPrenda(Long idPrenda);
    void deleteByPedidoIdPedido(Long idPedido);
    boolean existsByPrendaIdPrendaAndPedidoEstado(@Param("idPrenda") Long idPrenda, @Param("estado") String estado);
}
