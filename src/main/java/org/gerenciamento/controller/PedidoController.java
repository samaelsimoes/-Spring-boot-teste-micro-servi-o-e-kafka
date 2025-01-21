package org.gerenciamento.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.dto.PedidoDto;
import org.gerenciamento.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos na aplicação")
public class PedidoController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Cadastrar um pedido", description = "Cadastra um novo pedido")
    public PedidoDto cadastrarPedido(@RequestBody PedidoDto pedidoDTO) {
        return orderService.cadastrarPedido(pedidoDTO);
    }

    @PostMapping("/externo")
    @Operation(summary = "Receber pedido do produto Externo A", description = "Recebe um pedido enviado pelo Produto Externo A e calcula o valor total")
    public PedidoDto receberPedidoExterno(@RequestBody OrdemDto ordemDto) {
        return orderService.processarPedidoExterno(ordemDto);
    }

    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Recupera todos os pedidos cadastrados na base de dados")
    public List<PedidoDto> listarPedidos() {
        return orderService.listarPedidos();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar pedidos por status", description = "Recupera pedidos filtrados por status")
    public List<PedidoDto> listarPedidosPorStatus(@PathVariable String status) {
        return orderService.listarPedidosPorStatus(status);
    }


}