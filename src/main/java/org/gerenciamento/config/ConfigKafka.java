package org.gerenciamento.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.gerenciamento.model.Ordem;
import org.gerenciamento.model.Produto;
import org.gerenciamento.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.core.ConsumerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@AllArgsConstructor
@NoArgsConstructor
public class ConfigKafka {

    private String bootstrapServers = "localhost:9092";

    @Autowired
    private OrderService orderService;

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties("ordens");
        containerProps.setMessageListener(new MessageListener<String, String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> record) {
                processarOrdem(record.value()); // Chama o método para processar a ordem
            }
        });

        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }

    @Transactional
    public void processarOrdem(String ordemJson) {
        try {
            Ordem ordem = convertToOrdem(ordemJson);

            if (orderService.buscarOrdemPorId(ordem.getId()) != null) {
                throw new IllegalStateException("Ordem já existente com ID: " + ordem.getId());
            }

            BigDecimal total = BigDecimal.ZERO;
            for (Produto produto : ordem.getProdutos()) {
                total = total.add(produto.getPreco());
            }
            ordem.setValorTotal(total);

            orderService.saveOrUpdateOrdem(ordem);

            for (Produto produto : ordem.getProdutos()) {
                produto.setOrdem(ordem);
                    orderService.saveProduto(produto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar a ordem: " + e.getMessage());
        }
    }

    private Ordem convertToOrdem(String ordemJson) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(ordemJson, Ordem.class);
    }
}