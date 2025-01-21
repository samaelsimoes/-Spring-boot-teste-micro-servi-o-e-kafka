package org.gerenciamento.repository;

import org.gerenciamento.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByIdIn(List<Long> ids);
}