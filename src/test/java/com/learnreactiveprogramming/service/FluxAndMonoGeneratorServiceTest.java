package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void testFlux() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(nameFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void testFluxMap() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxMap();
        StepVerifier.create(nameFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void testFluxImmutability() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxImmutability();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh").verifyComplete();
    }

    @Test
    void testFluxFilter() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxFilter(5);
        StepVerifier.create(nameFlux).expectNext("6-MUKESH", "6-SURESH").verifyComplete();
    }


    @Test
    void textFluxFlatMap() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMap().log();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatMapDelay() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMap_AsyncDelay().log();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatConcatMapDelay() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAsyncDelay();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatConcatMapDelay_1() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAsyncDelay();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatConcatMapDelay_VirtualTimer() {
        VirtualTimeScheduler.getOrSet();
        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAsyncDelay();
        StepVerifier.withVirtualTime(() -> nameFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }


    @Test
    void textFluxTransform() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxTransform(4);
        StepVerifier.create(nameFlux).expectNext("M", "U", "K", "E", "S", "H").verifyComplete();
    }

    @Test
    void textFluxTransformNegative() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxTransform_SwitchIfEmpty(6);
        StepVerifier.create(nameFlux).expectNext("D", "E", "F", "A", "U", "L", "T").verifyComplete();
    }


    @Test
    void testMono() {
        var nameFlux = fluxAndMonoGeneratorService.namesMono();
        StepVerifier.create(nameFlux).expectNextCount(1).verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        var nameFlux = fluxAndMonoGeneratorService.namesMonoFlatMap();
        StepVerifier.create(nameFlux).expectNext(Arrays.asList("TIGER".split(""))).verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        var nameFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany();
        StepVerifier.create(nameFlux).expectNext("TIGER".split("")).verifyComplete();
    }

    @Test
    void exploreConcat() {
        var nameFlux = fluxAndMonoGeneratorService.exploreConcat();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Mahesh").verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        var nameFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Rasna").verifyComplete();
    }

    @Test
    void mergeDemo() {
        var nameFlux = fluxAndMonoGeneratorService.mergeDemo();
        StepVerifier.create(nameFlux).expectNext("Roshan", "Mukesh", "Sohan", "Mohan").verifyComplete();
    }

    @Test
    void mergeWithMonoDemo() {
        var nameFlux = fluxAndMonoGeneratorService.mergeWithMonoDemo();
        StepVerifier.create(nameFlux).expectNext("Roshan", "Data", "Mohan", "Raman").verifyComplete();
    }

    @Test
    void namesFlux_Exceptions() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_Exceptions().log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh", "Ramesh")
                .expectError(RuntimeException.class).verify();
    }

    @Test
    void namesFlux_ErrorReturn() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_OnErrorReturn().log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh", "Ramesh", "Hello").verifyComplete();
    }

    @Test
    void namesFlux_OnErrorResume() {

        var ex = new IllegalStateException("Not a valid state ");
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_OnErrorResume(ex).log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh", "Ramesh", "Default Error Message").verifyComplete();

    }

    @Test
    void namesFlux_OnErrorResume_1() {

        var ex = new RuntimeException("Not a valid state ");
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_OnErrorResume(ex).log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh", "Ramesh")
                .expectError(RuntimeException.class).verify();
    }

    @Test
    void namesFlux_OnErrorContinue() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_OnErrorContinue().log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Ramesh").verifyComplete();
    }

    @Test
    void namesFlux_OnErrorMap() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_OnErrorMap().log();
        StepVerifier.create(nameFlux).expectNext("Mukesh").expectError(ReactorException.class).verify();
    }

    @Test
    void namesFlux_doOnError() {
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_doError().log();
        StepVerifier.create(nameFlux).expectNext("Mukesh", "Suresh", "Ramesh").expectError(IllegalStateException.class).verify();
    }

    @Test
    void namesMono_onErrorReturn() {
        var nameFlux = fluxAndMonoGeneratorService.namesMono_ErrorReturn().log();
        StepVerifier.create(nameFlux).expectNext("abc").verifyComplete();
    }

    @Test
    void exception_mono_onErrorContinue() {
        var nameMono = fluxAndMonoGeneratorService.exception_mono_onErrorContinue("abc").log();
        StepVerifier.create(nameMono).verifyComplete();
    }


    @Test
    void exception_mono_onErrorContinue1() {
        var nameMono = fluxAndMonoGeneratorService.exception_mono_onErrorContinue("reactor").log();
        StepVerifier.create(nameMono).expectNext("reactor").verifyComplete();
    }
}
