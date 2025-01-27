package org.gerenciamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.gerenciamento.dto.OrdemDto;
import org.gerenciamento.dto.ProdutoDto;
import org.gerenciamento.exceptions.CustomException;
import org.gerenciamento.exceptions.OrdemNaoEncontradaException;
import org.gerenciamento.model.Ordem;
import org.gerenciamento.model.Produto;
import org.gerenciamento.repository.OrdemRepository;
import org.gerenciamento.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class OrderService {

    @Autowired
    private OrdemRepository ordemRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @CachePut(value = "ordens", key = "#ordemDTO.id")
    public OrdemDto cadastrarOrdem(OrdemDto ordemDTO) {
        logger.info("Iniciando cadastro de uma nova ordem com ID: {}" + ordemDTO.getId());

        Ordem ordem = new Ordem();
        ordem.setStatus(ordemDTO.getStatus());
        ordem.setValorTotal(calcularValorTotal(ordemDTO.getProdutoIds()));

        List<Produto> produtos = produtoRepository.findAllById(ordemDTO.getProdutoIds());
        if (produtos.isEmpty()) {
            throw new CustomException("PRODUTO_NAO_ENCONTRADO", "Nenhum produto encontrado para os IDs fornecidos.");
        }

        ordem.setProdutos(produtos);

        ordemRepository.save(ordem);
        logger.info("Ordem cadastrada com sucesso. ID: {}" + ordem.getId());

        return toOrdemDTO(ordem);
    }

    @Scheduled(fixedRate = 60000)
    public void receberPedidosExternosA() {
        logger.info("Recebendo pedidos do Sistema A...");
        List<OrdemDto> pedidosRecebidos = integrarComSistemaA();
        for (OrdemDto pedido : pedidosRecebidos) {
            cadastrarOrdem(pedido);
        }
    }

    public List<OrdemDto> integrarComSistemaA() {

        List<OrdemDto> ordensDoSistemaA = new ArrayList<>();

        OrdemDto ordem = new OrdemDto();
        ordem.setId(1L);
        ordem.setStatus("Pendente");
        ordem.setProdutoIds(Arrays.asList(1L, 2L));
        ordensDoSistemaA.add(ordem);

        return ordensDoSistemaA;
    }

    public void integrarComSistemaB(List<OrdemDto> ordens) {
        for (OrdemDto ordemDto : ordens) {
            Ordem ordem = toOrdem(ordemDto);
            logger.info("Enviando ordem ID: {} para o Sistema B.", ordem.getId());
            enviarOrdemParaKafka(ordem);
        }
    }

    private Ordem toOrdem(OrdemDto ordemDto) {
        Ordem ordem = new Ordem();
        ordem.setId(ordemDto.getId());
        ordem.setStatus(ordemDto.getStatus());
        ordem.setValorTotal(ordemDto.getValorTotal());
        List<Produto> produtos = produtoRepository.findAllById(ordemDto.getProdutoIds());
        ordem.setProdutos(produtos);
        return ordem;
    }

    private BigDecimal calcularValorTotal(List<Long> produtoIds) {
        logger.info("Calculando valor total para os produtos: {}", produtoIds);
        List<Produto> produtos = produtoRepository.findAllById(produtoIds);
        if (produtos.isEmpty()) {
            throw new CustomException("PRODUTO_NAO_ENCONTRADO", "Nenhum produto encontrado para os IDs fornecidos.");
        }
        return produtos.stream().map(Produto::getPreco).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrdemDto toOrdemDTO(Ordem ordem) {
        OrdemDto dto = new OrdemDto();
        dto.setId(ordem.getId());
        dto.setStatus(ordem.getStatus());
        dto.setProdutoIds(ordem.getProdutos().stream().map(Produto::getId).collect(Collectors.toList()));
        dto.setValorTotal(ordem.getValorTotal());
        return dto;
    }

    public ProdutoDto cadastrarProduto(ProdutoDto produtoDTO) {
        logger.info("Cadastrando novo produto: {}" + produtoDTO.getNome());

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());

        Produto produtoSalvo = produtoRepository.save(produto);
        logger.info("Produto cadastrado com sucesso. ID: {}" + produtoSalvo.getId());

        return toProdutoDTO(produtoSalvo);
    }

    public ProdutoDto buscarProdutoPorId(Long id) {
        logger.info("Buscando produto com ID: {}" + id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new CustomException("PRODUTO_NAO_ENCONTRADO", "Produto não encontrado com o ID: " + id));

        return toProdutoDTO(produto);
    }

    public OrdemDto buscarOrdemPorId(Long id) {
        logger.info("Buscando ordem com ID: {}" + id);
        Ordem ordem = ordemRepository.findById(id)
                .orElseThrow(() -> new OrdemNaoEncontradaException("Ordem não encontrada com o ID: " + id));

        return toOrdemDTO(ordem);
    }

    public List<ProdutoDto> listarProdutos() {
        logger.info("Listando todos os produtos.");

        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream().map(this::toProdutoDTO).collect(Collectors.toList());
    }

    private ProdutoDto toProdutoDTO(Produto produto) {
        ProdutoDto dto = new ProdutoDto();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        return dto;
    }

    @Cacheable(value = "ordens", key = "'todos'")
    public List<OrdemDto> listarOrdens() {
        logger.info("Listando todas as ordens.");
        List<Ordem> ordens = ordemRepository.findAll();
        return ordens.stream().map(this::toOrdemDTO).collect(Collectors.toList());
    }

    public void enviarOrdemParaKafka(Ordem ordem) {
        try {
            String ordemJson = convertToJson(ordem);
            kafkaTemplate.send("ordens", ordemJson);
            logger.info("Ordem enviada para o Kafka: " + ordem.getId());
        } catch (Exception e) {
            logger.error("Erro ao enviar ordem para o Kafka", e);
        }
    }

    public void processarOrdem(String ordemJson) {
        try {
            Ordem ordem = convertToOrdem(ordemJson);

            if (!ordemRepository.existsById(ordem.getId())) {
                BigDecimal valorTotal = calcularTotalOrdem(ordem.getProdutos());
                ordem.setValorTotal(valorTotal);

                ordemRepository.save(ordem);
            } else {
                System.out.println("A ordem com ID " + ordem.getId() + " já foi processada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BigDecimal calcularTotalOrdem(List<Produto> produtos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Produto produto : produtos) {
            total = total.add(produto.getPreco()); //
        }
        return total;
    }

    private String convertToJson(Ordem ordem) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(ordem);
    }

    private Ordem convertToOrdem(String ordemJson) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(ordemJson, Ordem.class);
    }

    @Transactional
    public Ordem saveOrUpdateOrdem(Ordem ordem) {
        return ordemRepository.save(ordem);
    }

    @Transactional
    public Produto saveProduto(Produto produto) {
        return produtoRepository.save(produto);
    }
}
