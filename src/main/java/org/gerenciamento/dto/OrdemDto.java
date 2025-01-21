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
public class OrdemDto {
    private Long id;
    private String status;
    private Long pedidoId;
    private List<Long> produtoIds;
    private BigDecimal valorTotal;
}
