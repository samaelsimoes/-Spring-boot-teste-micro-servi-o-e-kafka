package org.gerenciamento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDto {

    private Long id;
    private String status;
    private List<Long> produtoIds;
    private BigDecimal valorTotal;


    public PedidoDto(Long id, String status, BigDecimal valorTotal) {
        this.id = id;
        this.status = status;
        this.valorTotal = valorTotal;
    }
}