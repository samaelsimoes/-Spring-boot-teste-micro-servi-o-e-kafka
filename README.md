 **Gerenciamento de Pedidos**

Este projeto é uma aplicação para **gerenciamento de pedidos**, desenvolvida utilizando **Spring Boot**. Ele permite a criação, leitura, atualização e deleção de pedidos, utilizando o banco de dados **MySQL** para persistência de dados.

## **Tecnologias Utilizadas**

- **Java 17**
- **Spring Boot 3.1.3**
- **MySQL**
- **Maven**
- **JPA (Java Persistence API)**
- **Spring Security**
- **OAuth2** (para autenticação)
- **Redis** (para cache)
- **Kafka** (para mensageria)
- **JUnit e Mockito** (para testes)
- **Swagger UI** (para documentação da API)

## **Pré-requisitos**

- **JDK 17** ou superior
- **Maven 3.x**
- **MySQL** instalado e configurado
- **Redis** (caso queira utilizar cache)
- **Docker** (para facilitar o setup de dependências como Kafka e Redis)

## **Configuração do Banco de Dados**

O projeto utiliza **MySQL** como banco de dados. Para configurar o banco, siga os seguintes passos:

1. Crie um banco de dados no MySQL:
    ```sql
    CREATE DATABASE gerenciamento_pedidos;
    ```

2. No arquivo `application.properties`, configure a conexão com o banco de dados:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/gerenciamento_pedidos
    spring.datasource.username=root
    spring.datasource.password=your_password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```
**Endpoints**
A aplicação disponibiliza alguns endpoints principais para interação com os pedidos:

POST /api/pedidos - Cria um novo pedido
GET /api/pedidos/{id} - Recupera um pedido pelo ID
PUT /api/pedidos/{id} - Atualiza um pedido existente
DELETE /api/pedidos/{id} - Deleta um pedido pelo ID
Autenticação
A aplicação utiliza OAuth2 para autenticação. O processo de login é feito por meio de um provedor OAuth2 e a autenticação é realizada através de tokens JWT.

**Documentação da API**
A documentação da API pode ser acessada através do Swagger UI. Após iniciar a aplicação, acesse a URL: http://localhost:8080/swagger-ui/index.html

**Configuração do Kafka e Redis com Docker**
Para facilitar a configuração do Kafka e Redis, você pode utilizar o Docker para levantar esses serviços rapidamente. Aqui estão os passos para configurar ambos.

**1. Subindo Kafka com Docker**
O Kafka é usado para a comunicação assíncrona entre sistemas. Para rodá-lo localmente com Docker, siga os passos abaixo:
- Crie um arquivo docker-compose.yml na raiz do projeto

**Execute o Docker Compose**
Para subir os containers, no diretorio que foi criado docker-compose.yml
- docker-compose up -d

**Endpoints**
A aplicação disponibiliza os seguintes endpoints principais para interação com os pedidos:

POST /api/pedidos - Cria um novo pedido
GET /api/pedidos/{id} - Recupera um pedido pelo ID
PUT /api/pedidos/{id} - Atualiza um pedido existente
DELETE /api/pedidos/{id} - Deleta um pedido pelo ID


URL do Kafka Broker: localhost:9092
URL Swagger: http://localhost:8080/swagger-ui/index.html
