package org.gerenciamento.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens")
@Tag(name = "Ordens", description = "Gerenciamento de ordens na aplicação")
public class OrdemController {

    @Autowired
    private OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @PostMapping
    @Operation(summary = "Cadastrar uma ordem", description = "Cadastra uma nova ordem")
    public OrdemDto cadastrarOrdem(@RequestBody OrdemDto ordemDTO) {
        return orderService.cadastrarOrdem(ordemDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as ordens", description = "Recupera todas as ordens cadastradas na base de dados")
    public List<OrdemDto> listarOrdens() {
        return orderService.listarOrdens();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem por ID", description = "Recupera uma ordem específica pelo ID")
    public OrdemDto buscarOrdemPorId(@PathVariable Long id) {
        return orderService.buscarOrdemPorId(id);
    }

    @PostMapping("/receber-pedidos-a")
    @Operation(summary = "Receber pedidos do Sistema A", description = "Inicia a recepção de pedidos externos do Sistema A")
    public void receberPedidosExternosA() {
        orderService.receberPedidosExternosA();
    }

    @Scheduled(fixedRate = 60000)
    public void enviarPedidosExternosB() {
        logger.info("Enviando pedidos para o Sistema B...");
        List<OrdemDto> ordens = orderService.listarOrdens();
        orderService.integrarComSistemaB(ordens);
    }
}