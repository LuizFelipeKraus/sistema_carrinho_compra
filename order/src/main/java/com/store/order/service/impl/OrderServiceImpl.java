package com.store.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.order.domain.Order;
import com.store.order.domain.OrderItem;
import com.store.order.repository.OrderItemRepository;
import com.store.order.repository.OrderRepository;
import com.store.order.service.OrderService;

import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@Component
public class OrderServiceImpl extends GenericServiceImpl<Order, Long, OrderRepository> implements OrderService {
    private final WebClient userWebClient;
    private final WebClient productWebClient;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final AmqpTemplate rabbitTemplate;

    public OrderServiceImpl(OrderRepository repository, WebClient userWebClient, WebClient productWebClient, AmqpTemplate rabbitTemplate){
        super(repository);
        this.userWebClient = userWebClient;
        this.productWebClient = productWebClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void save(Order order) {
        validateUser(order.getUser_id());

        for (OrderItem item : order.getOrderItems()) {
            validateProduct(item.getProduct_id());
            updateProductQuantity(item.getProduct_id(), 1);
        }

        Order savedOrder = repository.save(order);

        for (OrderItem item : savedOrder.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct_id(item.getProduct_id());
            orderItemRepository.save(orderItem);
        }

        sendNotification(savedOrder);
    }

    private void validateProduct(Long productId) {
        this.productWebClient.get()
                .uri("/product/" + String.valueOf(productId))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        throw new RuntimeException("Produto não encontrado com o ID: " + productId);
                    }
                    return response.toEntity(String.class);
                }).block();
    }

    private void updateProductQuantity(Long productId, Integer newQuantity) {
        this.productWebClient.put()
                .uri("/product/" + String.valueOf(productId) + "/updateQuantity?quantity=" + String.valueOf(newQuantity))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("quantity", newQuantity)))
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        throw new RuntimeException("Falha ao atualizar a quantidade do produto com o ID: " + productId);
                    }
                    return response.toEntity(String.class);
                }).block();
    }

    private void validateUser(Long userId) {
        this.userWebClient.get()
                .uri("/user/" + String.valueOf(userId))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        throw new RuntimeException("Usuário não encontrado com o ID: " + userId);
                    }
                    return response.toEntity(String.class);
                }).block();
    }


    public void sendNotification(Order order){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(order);

            rabbitTemplate.convertAndSend(exchange, routingKey, json);
        }catch(JsonProcessingException e){}
    }

    public void updateStatus(Long orderId, Integer status ){
        Order order = get(orderId, "Ordem de serviço não encontrado com o ID: ");
        switch (status) {
            case 2:
                order.setStatusPagemento("Status: Esperando Pagamento");
                order.setStatus(status);
                update(order);
                break;
            case 3:
                order.setStatusPagemento("Status: Enviado Para Pagamento");
                order.setStatus(status);
                update(order);
                break;
            case 4:
                order.setStatusPagemento("Status: Pago");
                order.setStatus(status);
                update(order);
                break;
            case 5:
                if (order.getStatus() == 4){
                    order.setStatusPagemento("Status: Ordem Cancelada depois do Pagamento, Restituição em andamento!");
                }else{
                    order.setStatusPagemento("Status: Ordem Cancelada");
                }
                order.setStatus(status);
                update(order);
                break;
            default:
                break;
        }
    }
}

