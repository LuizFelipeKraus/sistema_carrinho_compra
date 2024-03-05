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

    public RabbitMQComponentImpl(WebClient userWebClient, WebClient productWebClient ) {
        this.userWebClient = userWebClient;
        this.productWebClient = productWebClient;
    }

    @RabbitListener(queues = "order_notification")
    public void handleMessage(String message){
        Map<String, Object> obj = emailServiceImpl.convertToObject(message);
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) obj.get("orderItems"); 

        int user_id = (int) obj.get("user_id");

        String response_user = this.userWebClient.get()
                .uri("/user/" + String.valueOf(user_id))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Map<String, Object> user = emailServiceImpl.convertToObject(response_user);

        for (Map<String, Object> orderItem : orderItems) {
            int product_id = (int) orderItem.get("product_id");
            String response_product = this.productWebClient.get()
                    .uri("/product/" + String.valueOf(product_id))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Map<String, Object> product = emailServiceImpl.convertToObject(response_product);
            String content = emailServiceImpl.constructOrderContent((String) product.get("name"), (String) user.get("name"));

            emailServiceImpl.sendEmail(content, (String) user.get("email"), "Notificação XPTO");
        }
    }
}
