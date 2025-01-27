package org.gerenciamento.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gerenciamento.dto.ProdutoDto;
import org.gerenciamento.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Gerenciamento de produtos na aplicação")
public class ProdutoController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Cadastrar um produto", description = "Cadastra um novo produto")
    public ProdutoDto cadastrarProduto(@RequestBody ProdutoDto produtoDTO) {
        return orderService.cadastrarProduto(produtoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Recupera todos os produtos cadastrados na base de dados")
    public List<ProdutoDto> listarProdutos() {
        return orderService.listarProdutos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Recupera um produto específico pelo seu ID")
    public ProdutoDto buscarProdutoPorId(@PathVariable Long id) {
        return orderService.buscarProdutoPorId(id);
    }
}