package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MonoAndFluxPracticeTest {

    @Test
    public void monoAndFluxPractice() {
        Flux extracted = MonoAndFluxPractice.extracted();
        StepVerifier.create(extracted).expectNext("Hello", "World").verifyComplete();
    }

}
