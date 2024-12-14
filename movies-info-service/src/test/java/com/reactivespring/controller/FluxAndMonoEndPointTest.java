package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Flux.create;


@WebFluxTest(controllers = FluxAndMonoEndPoint.class)
@AutoConfigureWebTestClient
class FluxAndMonoEndPointTest {

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testFlux() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FluxAndMonoEndPoint.Person.class
                );
    }

    @Test
    void testFluxApproach02() {
        Flux<FluxAndMonoEndPoint.Person> responseBody = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(FluxAndMonoEndPoint.Person.class).getResponseBody();

        StepVerifier.create(responseBody).assertNext(person -> assertEquals(1, person.getId())).thenCancel().verify();

    }


    @Test
    void testFluxApproach03() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk().expectBodyList(FluxAndMonoEndPoint.Person.class).consumeWith(listEntityExchangeResult -> {
                    List<FluxAndMonoEndPoint.Person> responseBody1 = listEntityExchangeResult.getResponseBody();
                    assert responseBody1 != null;
                    assertEquals(100, responseBody1.size());
                });
    }


    @Test
    void demoFluxStream() {
        webTestClient.get()
                .uri("/fluxStream")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testStreamApproach02() {
        Flux<Long> responseBody = webTestClient.get()
                .uri("/fluxStream")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.withVirtualTime(() -> responseBody)
                .thenAwait(Duration.ofSeconds(4)) // Adjust the duration based on your stream's timing
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }

}
