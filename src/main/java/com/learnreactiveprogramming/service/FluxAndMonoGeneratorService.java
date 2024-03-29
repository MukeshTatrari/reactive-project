package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Mukesh", "Suresh"));
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Mukesh", "Suresh")).map(String::toUpperCase).doOnComplete(() -> System.out.println("hello completed"));
    }

    public Flux<String> namesFluxImmutability() {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh", "Suresh"));
        input.map(String::toUpperCase);
        return input;
    }

    public Flux<String> namesFluxFilter(int size) {
        Flux<String> input = Flux.fromIterable(List.of("Mukesh", "Suresh"));
        return input.map(String::toUpperCase).filter(e -> e.length() > size).map(s -> s.length() + "-" + s).doOnNext(e -> {
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
        return Flux.just("Mukesh", "Suresh", "Ramesh").concatWith(Flux.error(new RuntimeException("Exception while Processing flux"))).concatWith(Flux.just("Hello"));
    }

    public Flux<String> namesFlux_OnErrorReturn() {
        return Flux.just("Mukesh", "Suresh", "Ramesh").concatWith(Flux.error(new RuntimeException("Exception while Processing flux"))).onErrorReturn("Hello");
    }

    public Mono<String> exception_mono_onErrorContinue(String input) {
        var monoInput = Mono.just(input);
        return monoInput.map(name -> {
            if ("abc".equalsIgnoreCase(name)) {
                throw new RuntimeException("Exception");
            }
            return name;
        }).onErrorContinue((ex, name) -> {
            log.info("Exception is :: " + ex);
            log.info("Name is :: " + name);
        });

    }

    public Flux<String> namesFlux_OnErrorResume(Exception e) {
        var recoveryFlux = Flux.just("Default Error Message");
        return Flux.just("Mukesh", "Suresh", "Ramesh").concatWith(Flux.error(e)).onErrorResume(ex -> {
            log.info("Exception is :: " + e);
            if (e instanceof IllegalStateException) {
                return recoveryFlux;
            } else {
                return Flux.error(ex);
            }
        });
    }

    public Flux<String> namesFlux_OnErrorContinue() {
        return Flux.just("Mukesh", "Suresh", "Ramesh").map(name -> {
            if ("Suresh".equalsIgnoreCase(name)) {
                throw new IllegalStateException("Exception");
            }
            return name;
        }).onErrorContinue((ex, name) -> {
            log.info("Exception is :: " + ex);
            log.info("Name is :: " + name);
        });
    }

    public Flux<String> namesFlux_OnErrorMap() {
        return Flux.just("Mukesh", "Suresh", "Ramesh").map(name -> {
            if ("Suresh".equalsIgnoreCase(name)) {
                throw new IllegalStateException("Exception");
            }
            return name;
        }).onErrorMap(ex -> {
            log.info("Exception is :: " + ex);
            return new ReactorException(ex, ex.getMessage());
        });
    }

    public Flux<String> namesFlux_doError() {
        return Flux.just("Mukesh", "Suresh", "Ramesh").concatWith(Flux.error(new IllegalStateException("Exception occurred"))).doOnError(ex -> {
            log.error("Error e ::: " + ex);
        });
    }

    public Mono<Object> namesMono_ErrorReturn() {
        return Mono.just("Mukesh").map(e -> {
            throw new RuntimeException("Exception occurred");
        }).onErrorReturn("abc");
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
        return input.transform(filterMap).flatMap(s -> Flux.fromArray(s.split(""))).defaultIfEmpty("default").log();
    }

    public Flux<String> namesFluxTransform_SwitchIfEmpty(int size) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase).filter(e -> e.length() > size).flatMap(s -> Flux.fromArray(s.split("")));

        Flux<String> input = Flux.fromIterable(List.of("Mukesh"));
        Flux<String> aDefault = Flux.just("Default").transform(filterMap);

        return input.transform(filterMap).switchIfEmpty(aDefault).log();
    }

    public Mono<String> namesMono() {
        return Mono.just("Tiger");

    }

    public Flux<String> exploreConcat() {
        return Flux.concat(Flux.just("Mukesh"), Flux.just("Mahesh")).log();
    }

    public Flux<String> exploreConcatDelay() {
        return Flux.fromIterable(List.of("Alex", "Bob")).map(String::toUpperCase).filter(s -> s.length() > 3).concatWith(s -> splitString_withDelay(String.valueOf(s)));
    }

    private Flux<String> splitString_withDelay(String name) {
        var delay = new Random().nextInt(1000);
        var charArray = name.split("");
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(1000));
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
        return Flux.merge(Flux.just("Mohan", "Raman").delayElements(Duration.ofMillis(1000)), Flux.just("Roshan", "Data")).delayElements(Duration.ofMillis(1500));
    }


    public Flux<Integer> explore_Generate() {
        return Flux.generate(() -> 1, (state, sink) -> {
            sink.next(state * 2);
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        });
    }

    public static List<String> names() {
        delay(1000);
        return List.of("Alex", "Bob", "Charley");
    }

    public Flux<String> explore_create() {
        return Flux.create(sink -> {
//            names().forEach(sink::next);
            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names().forEach(sink::next);
                    })
                    .thenRun(() -> explore_sendEvents(sink));
        });
    }

    public Mono<String> explore_create_mono() {
        return Mono.create(sink -> {
            sink.success("Alex");
        });
    }

    public Flux<String> explore_handle() {
        return Flux.fromIterable(List.of("Alex", "Bob", "Charley"))
                .handle((name, sink) -> {
                    if (name.length() > 3) {
                        sink.next(name.toUpperCase());
                    }
                });
    }

    public void explore_sendEvents(FluxSink<String> fluxSink) {
        CompletableFuture
                .supplyAsync(() -> names())
                .thenAccept(names -> {
                    names().forEach(fluxSink::next);
                }).thenRun(fluxSink::complete);
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
