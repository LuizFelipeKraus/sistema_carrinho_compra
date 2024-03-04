# Microservices Project

Este projeto consiste em três microserviços que lidam com pedidos, produtos e autenticação. Cada microserviço tem sua própria API e responsabilidade específica.

## Microserviços

1. **Order Service**
   - URL: [http://localhost:8084/api/order](http://localhost:8084/api/order)
   - Exemplo de pedido:
     ```json
     {
        "user_id" : 1,
        "orderItems":[
            {
                "product_id": 9
            },
            {
                "product_id": 1
            }
        ]
     }
     ```
     - Utiliza EUREKA e RabbitMQ para o envio de e-mails em filas.

2. **Product Service**
   - URL: [http://localhost:8086/api/product](http://localhost:8086/api/product)
   - Exemplo de produto:
     ```json
     {
        "name":"Laptop ",
        "describe":"Powerful laptop for professional use",
        "valor":1299.99,
        "quantidade":100
     }
    ```

3. **Auth Service**
   - URL: [http://localhost:8082/api/user](http://localhost:8082/api/user)
   

## Pré-requisitos

- Java 8 ou superior instalado
- EUREKA Server configurado
- RabbitMQ configurado

## Configuração

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/LuizFelipeKraus/sistema_carrinho_compra.git

## Execução

1. Execute o EUREKA Server:
   ```bash
   cd eureka
   ./mvnw spring-boot:run

2. Execute o NOTIFICATION Server:
   ```bash
   cd notification
   ./mvnw spring-boot:run

3. Execute o ORDER Server:
   ```bash
    cd order
    ./mvnw spring-boot:run

4. Execute o PRODUCT Server:
   ```bash
   cd product
   ./mvnw spring-boot:run

5. Execute o AUTH Server:
   ```bash
   cd auth
   ./mvnw spring-boot:run