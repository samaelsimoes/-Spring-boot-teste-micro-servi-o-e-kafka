package org.gerenciamento.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "ordens";  // Nome do tópico do Kafka

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Método para enviar a ordem para o Kafka
    public void sendOrderToKafka(String orderId) {
        // Envia a ordem para o tópico
        kafkaTemplate.send(TOPIC, orderId);
        System.out.println("Enviando ordem ID: " + orderId + " para o Kafka");
    }
}