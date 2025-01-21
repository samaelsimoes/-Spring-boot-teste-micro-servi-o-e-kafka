package org.gerenciamento.repository;

import org.gerenciamento.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatus(String status);

    public boolean existsByStatusAndValorTotalAndProdutosIn(String status, BigDecimal valorTotal, List<Long> produtoIds);
}