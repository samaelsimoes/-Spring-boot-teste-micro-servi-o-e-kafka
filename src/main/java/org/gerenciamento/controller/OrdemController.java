package org.gerenciamento.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens")
@Tag(name = "Ordens", description = "Gerenciamento de ordens na aplicação")
public class OrdemController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Cadastrar uma ordem", description = "Cadastra uma nova ordem vinculada a um pedido")
    public OrdemDto cadastrarOrdem(@RequestBody OrdemDto ordemDTO) {
        return orderService.cadastrarOrdem(ordemDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as ordens", description = "Recupera todas as ordens cadastradas na base de dados")
    public List<OrdemDto> listarOrdens() {
        return orderService.listarOrdens();
    }
}