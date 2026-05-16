package com.rahul.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        // Generate unique request id
        String requestId = UUID.randomUUID().toString();

        // Mutate request and add header
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest()
                        .mutate()
                        .header("X-Request-ID", requestId)
                        .build())
                .build();

        // Logging
        LOGGER.info("Incoming Request -> {} {} RequestId={}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(),
                requestId);

        // Continue filter chain
        return chain.filter(modifiedExchange);
    }
}