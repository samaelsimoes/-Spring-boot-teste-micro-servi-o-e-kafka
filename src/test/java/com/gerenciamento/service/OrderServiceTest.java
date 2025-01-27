package com.gerenciamento.service;

import lombok.RequiredArgsConstructor;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.dto.ProdutoDto;
import org.gerenciamento.exceptions.CustomException;
import org.gerenciamento.exceptions.OrdemNaoEncontradaException;
import org.gerenciamento.model.Ordem;
import org.gerenciamento.model.Produto;
import org.gerenciamento.repository.OrdemRepository;
import org.gerenciamento.repository.ProdutoRepository;
import org.gerenciamento.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private OrdemRepository ordemRepository;

    @MockBean
    private ProdutoRepository produtoRepository;

    private Ordem ordem;
    private Produto produto;

    @Before
    public void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(new BigDecimal("50.00"));

        ordem = new Ordem();
        ordem.setId(1L);
        ordem.setStatus("Pendente");
        ordem.setValorTotal(new BigDecimal("100.00"));
        ordem.setProdutos(Arrays.asList(produto));
    }

    @Test
    public void testEnviarOrdemParaKafka() throws Exception {
        String ordemJson = "{\"id\": 1, \"status\": \"Pendente\", \"valorTotal\": 100.00}";
        orderService.enviarOrdemParaKafka(ordem);

        Mockito.verify(kafkaTemplate, times(1)).send(eq("ordens"), eq(ordemJson));
    }

    @Test
    public void testCadastrarOrdem() {
        OrdemDto ordemDto = new OrdemDto();
        ordemDto.setId(1L);
        ordemDto.setStatus("Pendente");
        ordemDto.setProdutoIds(Arrays.asList(1L));
        ordemDto.setValorTotal(new BigDecimal("100.00"));

        when(produtoRepository.findAllById(eq(ordemDto.getProdutoIds()))).thenReturn(Arrays.asList(produto));
        when(ordemRepository.save(any(Ordem.class))).thenReturn(ordem);

        OrdemDto result = orderService.cadastrarOrdem(ordemDto);

        Mockito.verify(ordemRepository, times(1)).save(any(Ordem.class));
        assert(result.getId().equals(1L));
    }

    @Test
    public void testCadastrarProduto() {
        ProdutoDto produtoDto = new ProdutoDto();
        produtoDto.setNome("Produto Teste");
        produtoDto.setPreco(new BigDecimal("50.00"));

        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDto result = orderService.cadastrarProduto(produtoDto);

        Mockito.verify(produtoRepository, times(1)).save(any(Produto.class));
        assert(result.getNome().equals("Produto Teste"));
    }

    @Test
    public void testBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDto result = orderService.buscarProdutoPorId(1L);

        assert(result.getId().equals(1L));
        assert(result.getNome().equals("Produto Teste"));
    }

    @Test(expected = CustomException.class)
    public void testBuscarProdutoPorIdNaoEncontrado() {
        when(produtoRepository.findById(2L)).thenReturn(Optional.empty());

        orderService.buscarProdutoPorId(2L);
    }

    @Test
    public void testBuscarOrdemPorId() {
        when(ordemRepository.findById(1L)).thenReturn(Optional.of(ordem));

        OrdemDto result = orderService.buscarOrdemPorId(1L);

        assert(result.getId().equals(1L));
        assert(result.getStatus().equals("Pendente"));
    }

    @Test(expected = OrdemNaoEncontradaException.class)
    public void testBuscarOrdemPorIdNaoEncontrada() {
        when(ordemRepository.findById(2L)).thenReturn(Optional.empty());

        orderService.buscarOrdemPorId(2L);
    }

    @Test
    public void testListarOrdens() {
        when(ordemRepository.findAll()).thenReturn(Arrays.asList(ordem));

        List<OrdemDto> result = orderService.listarOrdens();

        assert(result.size() == 1);
        assert(result.get(0).getId().equals(1L));
    }

    @Test
    public void testProcessarOrdem() throws Exception {
        Ordem ordemParaProcessar = new Ordem();
        ordemParaProcessar.setId(2L);
        ordemParaProcessar.setProdutos(Arrays.asList(produto));

        when(ordemRepository.existsById(2L)).thenReturn(false);
        when(ordemRepository.save(any(Ordem.class))).thenReturn(ordemParaProcessar);

        orderService.processarOrdem("{\"id\":2,\"status\":\"Pendente\",\"valorTotal\":100.00}");

        Mockito.verify(ordemRepository, times(1)).save(any(Ordem.class));
    }

    @Test
    public void testIntegrarComSistemaA() {
        List<OrdemDto> ordens = orderService.integrarComSistemaA();
        assert(ordens.size() > 0);
    }


}