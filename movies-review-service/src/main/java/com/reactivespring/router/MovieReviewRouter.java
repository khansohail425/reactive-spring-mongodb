package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class MovieReviewRouter {

    @Bean
    RouterFunction<ServerResponse> ping() {
        return route().GET("/api/v1/ping", request -> ServerResponse.ok().bodyValue("hello-world")).build();
    }
}
