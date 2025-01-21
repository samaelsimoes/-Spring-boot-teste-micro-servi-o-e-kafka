package org.gerenciamento.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String status;
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido")
    private List<Ordem> ordens;

    @OneToMany(mappedBy = "pedido")
    private List<Produto> produtos;

}