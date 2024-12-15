package com.reactivespring.router;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = MovieReviewRouter.class)
@AutoConfigureWebTestClient
class MovieReviewRouterTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void ping() {
        webTestClient.get().uri("/api/v1/ping").exchange().expectStatus().isOk().expectBody(String.class).consumeWith(stringEntityExchangeResult -> {
            String responseBody = stringEntityExchangeResult.getResponseBody();
            assertEquals("hello-world", responseBody);
        });
    }
}