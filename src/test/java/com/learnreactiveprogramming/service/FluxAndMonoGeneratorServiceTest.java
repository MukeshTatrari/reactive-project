package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

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
        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMap();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatMapDelay() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMap_AsyncDelay();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
    }

    @Test
    void textFluxFlatConcatMapDelay() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAsyncDelay();
        StepVerifier.create(nameFlux).expectNext("M", "u", "k", "e", "s", "h").verifyComplete();
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
        StepVerifier.create(nameFlux).expectNext("Mukesh","Mahesh").verifyComplete();
    }
    @Test
    void exploreConcatWithMono() {
        var nameFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();
        StepVerifier.create(nameFlux).expectNext("Mukesh","Rasna").verifyComplete();
    }

    @Test
    void mergeDemo() {
        var nameFlux = fluxAndMonoGeneratorService.mergeDemo();
        StepVerifier.create(nameFlux).expectNext("Mohan","Roshan").verifyComplete();
    }

    @Test
    void mergeWithMonoDemo() {
        var nameFlux = fluxAndMonoGeneratorService.mergeWithMonoDemo();
        StepVerifier.create(nameFlux).expectNext("Mohan","Roshan").verifyComplete();
    }
}
