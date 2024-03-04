package com.store.notification.component.impl;

import com.store.notification.component.RabbitMQComponent;
import com.store.notification.service.impl.EmailServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class RabbitMQComponentImpl  implements RabbitMQComponent {
    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    private final WebClient userWebClient;
    private final WebClient productWebClient;

    public RabbitMQComponentImpl(WebClient userWebClient, WebClient productWebClient) {
        this.userWebClient = userWebClient;
        this.productWebClient = productWebClient;
    }

    @RabbitListener(queues = "order_notification")
    public void handleMessage(Map<String, Object> message) {
        int user_id = (int) message.get("user_id");
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) message.get("orderItems");

        for (Map<String, Object> orderItem : orderItems) {
            int product_id = (int) orderItem.get("product_id");
            System.out.println("Processando pedido para o produto_id: " + product_id);
            String response_user = this.userWebClient.get()
                    .uri("/user/" + user_id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            String response_product = this.productWebClient.get()
                    .uri("/product/" + product_id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> user = emailServiceImpl.convertToObject(response_user);
            Map<String, Object> product = emailServiceImpl.convertToObject(response_product);

            String content = emailServiceImpl.constructOrderContent((String) product.get("name"), (String) user.get("username"));

            emailServiceImpl.sendEmail(content, (String) user.get("email"), "Notificação XPTO");
        }
    }

    @Override
    public void handleMessage(String message) {
        throw new UnsupportedOperationException("Unimplemented method 'handleMessage'");
    }
}
