package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Mukesh", "Suresh"));
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Mukesh", "Suresh"))
                .map(String::toUpperCase)
                .doOnComplete(() -> System.out.println("hello completed"));
    }

    public Flux<String> namesFluxImmutability() {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh", "Suresh"));
        input.map(String::toUpperCase);
        return input;
    }

    public Flux<String> namesFluxFilter(int size) {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh", "Suresh"));
        return input.map(String::toUpperCase)
                .filter(e -> e.length() > size)
                .map(s -> s.length() + "-" + s)
                .doOnNext(e -> {
                    System.out.println("Next value is ::: " + e);
                }).doOnSubscribe(s -> {
                    System.out.println("Subscription is :::: " + s);
                }).doOnComplete(() -> {
                    System.out.println("completed Successfully");
                }).doFinally(signalType -> {
                    System.out.println("signalType in side finally  " + signalType);
                });
    }

    public Flux<String> namesFlux_Exceptions() {
        return Flux.just("Mukesh", "Suresh", "Ramesh")
                .concatWith(Flux.error(new RuntimeException("Exception while Processing flux")))
                .concatWith(Flux.just("Hello"));
    }

    public Flux<String> namesFluxFlatMap() {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        return input.flatMap(e -> Flux.fromArray(e.split("")));
    }

    public Flux<String> namesFluxFlatMap_AsyncDelay() {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        var delay = new Random().nextInt(1000);
        return input.flatMap(e -> Flux.fromArray(e.split(""))).delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFluxConcatMapAsyncDelay() {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        var delay = 1000;
        return input.concatMap(e -> Flux.fromArray(e.split(""))).delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFluxTransform(int size) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase).filter(e -> e.length() > size);
        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        return input.transform(filterMap)
                .flatMap(s -> Flux.fromArray(s.split("")))
                .defaultIfEmpty("default").log();
    }

    public Flux<String> namesFluxTransform_SwitchIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> filterMap =
                name -> name.map(String::toUpperCase)
                        .filter(e -> e.length() > size)
                        .flatMap(s -> Flux.fromArray(s.split("")));

        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        Flux<String> aDefault = Flux.just("Default").transform(filterMap);

        return input.transform(filterMap)
                .switchIfEmpty(aDefault).log();
    }

    public Mono<String> namesMono() {
        return Mono.just("Tiger");

    }

    public Flux<String> exploreConcat() {
        return Flux.concat(Flux.just("Mukesh"), Flux.just("Mahesh")).log();
    }

    public Flux<String> exploreConcatWithMono() {
        return Mono.just("Mukesh").concatWith(Flux.just("Rasna")).log();
    }

    public Mono<List<String>> namesMonoFlatMap() {
        Mono<String> mono = Mono.just("Tiger").map(String::toUpperCase);
        return mono.flatMap(s -> Mono.just(Arrays.asList(s.split(""))));

    }

    public Flux<String> namesMonoFlatMapMany() {
        Mono<String> mono = Mono.just("Tiger").map(String::toUpperCase);
        return mono.flatMapMany(s -> Flux.fromArray(s.split(""))).log();

    }

    public Flux<String> mergeDemo() {

        var abcFlux = Flux.just("Mukesh", "Mohan").delayElements(Duration.ofMillis(150));

        var defFlux = Flux.just("Roshan", "Sohan").delayElements(Duration.ofMillis(100));

        return Flux.merge(abcFlux, defFlux).log();


    }


    public Flux<String> mergeWithMonoDemo() {
        return Flux.
                merge(Flux.just("Mohan", "Raman").delayElements(Duration.ofMillis(1000)),
                        Flux.just("Roshan", "Data")).delayElements(Duration.ofMillis(1500));
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();
//        service.namesFluxMap().subscribe(System.out::println);
//        service.namesMono().subscribe(System.out::println);
//        service.namesMonoFlatMap().subscribe(System.out::println);
//        service.namesFluxTransform_SwitchIfEmpty(6).subscribe(System.out::println);
        service.mergeDemo().subscribe(System.out::println);
    }
}
