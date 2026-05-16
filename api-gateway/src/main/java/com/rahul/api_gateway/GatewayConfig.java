package com.rahul.api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()

                // User Service
                .route("user-service", r -> r
                        .path("/user-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))

                // Transaction Service
                .route("transaction-service", r -> r
                        .path("/txn-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))

                // Wallet Service
                .route("wallet-service", r -> r
                        .path("/wallet-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8084"))

                // Notification Service
                .route("notification-service", r -> r
                        .path("/notification-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))

                .build();
    }
}