package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class MonoAndFluxPractice {


    public static Mono<List<String>> flatMapMono(String mono) {
        String[] split = mono.split("");
        return Mono.just(List.of(split));
    }

    public static Mono<String[]> flatMapMono2(String mono) {
        String[] split = mono.split("");
        return Mono.just(split);
    }




    public static void main(String[] args) {
//        Mono<String> mono = Mono.just("Hello");
//        mono.map(String::toUpperCase).flatMap(MonoAndFluxPractice::flatMapMono).subscribe(System.out::println);
//        System.out.println("++++++++++++++++++++++++++");
//        mono.map(String::toUpperCase).flatMap(MonoAndFluxPractice::flatMapMono2).map(Arrays::toString).subscribe(System.out::println);
//


        extracted();

    }

    public static Flux extracted() {
        return Flux.empty().log();
    }
}
