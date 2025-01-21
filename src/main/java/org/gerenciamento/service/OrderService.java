package org.gerenciamento.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.dto.PedidoDto;
import org.gerenciamento.dto.ProdutoDto;
import org.gerenciamento.model.Ordem;
import org.gerenciamento.model.Pedido;
import org.gerenciamento.model.Produto;
import org.gerenciamento.repository.OrdemRepository;
import org.gerenciamento.repository.PedidoRepository;
import org.gerenciamento.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@NoArgsConstructor
@Service
public class OrderService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private OrdemRepository ordemRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @CachePut(value = "pedidos", key = "#pedidoDTO.id")
    public PedidoDto cadastrarPedido(PedidoDto pedidoDTO) {
        Pedido pedidoExistente = pedidoRepository.findById(pedidoDTO.getId())
                .orElse(null);

        if (pedidoExistente != null) {
            throw new RuntimeException("Pedido já existe com o ID " + pedidoDTO.getId());
        }

        Pedido pedido = new Pedido();
        pedido.setStatus(pedidoDTO.getStatus());
        pedido.setValorTotal(pedidoDTO.getValorTotal());

        List<Produto> produtos = produtoRepository.findAllById(pedidoDTO.getProdutoIds());
        pedido.setProdutos(produtos);

        pedidoRepository.save(pedido);

        return pedidoDTO;
    }

    @CachePut(value = "ordens", key = "#ordemDTO.id")
    public OrdemDto cadastrarOrdem(OrdemDto ordemDTO) {
        Pedido pedido = pedidoRepository.findById(ordemDTO.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        Ordem ordem = new Ordem();
        ordem.setStatus(ordemDTO.getStatus());
        ordem.setPedido(pedido);
        ordem.setValorTotal(calcularValorTotal(ordemDTO.getProdutoIds()));
        ordemRepository.save(ordem);
        return toOrdemDTO(ordem);
    }

    @CachePut(value = "produtos", key = "#produtoDTO.id")
    public ProdutoDto cadastrarProduto(ProdutoDto produtoDTO, Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setPedido(pedido);
        produtoRepository.save(produto);
        return toProdutoDTO(produto);
    }

    @Cacheable(value = "pedidos", key = "#status")
    public List<PedidoDto> listarPedidosPorStatus(String status) {
        return pedidoRepository.findByStatus(status).stream()
                .map(pedido -> new PedidoDto(pedido.getId(), pedido.getStatus(), pedido.getValorTotal()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "produtos", key = "'todos'")
    public List<ProdutoDto> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream().map(this::toProdutoDTO).collect(Collectors.toList());
    }

    @Cacheable(value = "ordens", key = "'todos'")
    public List<OrdemDto> listarOrdens() {
        List<Ordem> ordens = ordemRepository.findAll();
        return ordens.stream().map(this::toOrdemDTO).collect(Collectors.toList());
    }

    @Cacheable(value = "pedidos", key = "#id")
    public PedidoDto getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return toPedidoDTO(pedido);
    }

    private BigDecimal calcularValorTotal(List<Long> produtoIds) {
        List<Produto> produtos = produtoRepository.findAllById(produtoIds);
        return produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PedidoDto toPedidoDTO(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(pedido.getId());
        dto.setStatus(pedido.getStatus());
        dto.setProdutoIds(pedido.getProdutos().stream().map(Produto::getId).collect(Collectors.toList()));
        dto.setValorTotal(pedido.getValorTotal());
        return dto;
    }

    private OrdemDto toOrdemDTO(Ordem ordem) {
        OrdemDto dto = new OrdemDto();
        dto.setId(ordem.getId());
        dto.setStatus(ordem.getStatus());
        dto.setPedidoId(ordem.getPedido().getId());
        dto.setProdutoIds(ordem.getProdutos().stream().map(Produto::getId).collect(Collectors.toList()));
        dto.setValorTotal(ordem.getValorTotal());
        return dto;
    }

    private ProdutoDto toProdutoDTO(Produto produto) {
        ProdutoDto dto = new ProdutoDto();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        return dto;
    }

    public List<PedidoDto> listarPedidos() {
        return pedidoRepository.findAll().stream()
                .map(pedido -> new PedidoDto(pedido.getId(), pedido.getStatus(), pedido.getValorTotal()))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "pedidos", key = "#ordemDto.pedidoId")
    public PedidoDto processarPedidoExterno(OrdemDto ordemDto) {
        Pedido pedido = new Pedido();
        pedido.setStatus(ordemDto.getStatus());

        BigDecimal valorTotal = calcularValorTotal(ordemDto.getProdutoIds());
        pedido.setValorTotal(valorTotal);

        pedidoRepository.save(pedido);

        return new PedidoDto(pedido.getId(), pedido.getStatus(), pedido.getValorTotal());
    }
}