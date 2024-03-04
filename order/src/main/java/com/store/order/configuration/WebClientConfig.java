package com.store.order.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${webclient.user.url}")
    private String userBaseUrl;

    @Value("${webclient.product.url}")
    private String productBaseUrl;

    @Bean(name = "userWebClient")
    @Autowired
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl(this.userBaseUrl).build();
    }

    @Bean(name = "productWebClient")
    @Autowired
    public WebClient productWebClient(WebClient.Builder builder) {
        return builder.baseUrl(this.productBaseUrl).build();
    }

}

