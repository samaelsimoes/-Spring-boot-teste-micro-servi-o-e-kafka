package org.gerenciamento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GerenciamentoPedidosApplication {

    private static final Logger log = LoggerFactory.getLogger(GerenciamentoPedidosApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GerenciamentoPedidosApplication.class, args);
    }

    @Bean
    public CommandLineRunner logApplicationStartup(@Value("${server.port:8080}") String port) {
        log.info("http://localhost:8080/swagger-ui/index.html");
        return args -> log.info("Sistema iniciado! Acesse: http://localhost:{}", port);
    }
}
