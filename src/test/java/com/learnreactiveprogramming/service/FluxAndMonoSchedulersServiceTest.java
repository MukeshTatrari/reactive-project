package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explore_WithoutPublisherOn() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_WithoutPublisherOn();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

    @Test
    void explore_PublisherOn() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_PublisherOn();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

    @Test
    void explore_PublisherOnBoundedElastic() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_BoundedElastic();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

    @Test
    void explore_SubscribeOnBoundedElastic() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_SubscribeOn();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

    @Test
    void explore_Parallel() {
        fluxAndMonoSchedulersService.explore_Parallel();
    }

    @Test
    void explore_Parallel_Using_FlatMap() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_Parallel_UsingFlatMap().log();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

    @Test
    void explore_Parallel_Using_FlatMapSequential() {
        final Flux<String> stringFlux = fluxAndMonoSchedulersService.explore_Parallel_UsingFlatMapSequential().log();
        StepVerifier.create(stringFlux).expectNextCount(6).verifyComplete();
    }

}