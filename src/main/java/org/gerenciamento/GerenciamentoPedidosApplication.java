package org.gerenciamento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class GerenciamentoPedidosApplication {

    private static final Logger logger = LoggerFactory.getLogger(GerenciamentoPedidosApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GerenciamentoPedidosApplication.class, args);
    }

    /**
     * Bean que executa ações ao iniciar a aplicação.
     *
     * @param port Porta configurada no application.properties ou valor padrão (8080).
     * @return CommandLineRunner que loga informações úteis sobre a aplicação.
     */
    @Bean
    public CommandLineRunner logApplicationStartup(@Value("${server.port:8080}") String port) {
        return args -> {
            logger.info("Sistema iniciado! Acesse: http://localhost:{}", port);
            logger.info("URL Swagger: http://localhost:{}/swagger-ui/index.html", port);
            logger.info("URL do Kafka Broker: localhost:9092");
        };
    }
}