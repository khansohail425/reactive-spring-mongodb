package com.reactivespring.controller;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.IntStream;

@RestController("flux-mono")
public class FluxAndMonoEndPoint {


    @GetMapping("/flux")
    public ResponseEntity<Flux<Person>> demoFlux() {


        Flux<Person> flux = Flux.fromIterable(IntStream.rangeClosed(1, 100).mapToObj(
                        id -> Person.builder().id(id).name("Sohail" + ((char) id)).age(26 + id - 1).mobile("+91-8225885153").build()
                ).toList())
                .log();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(flux);
    }

    @GetMapping(value = "/fluxStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<?>> demoFluxStream() {
        Flux<?> flux = Flux.interval(Duration.ofSeconds(1)).log();
        return ResponseEntity.ok()
                .body(flux);
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person {

        private String name;
        private int age;
        private String mobile;
        private int id;


    }
}
