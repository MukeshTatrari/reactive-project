package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    @Disabled
    void retrieveAllMovieInfo_RestClient() {
        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo_RestClient().log();
        StepVerifier.create(movieInfoFlux).expectNextCount(7).verifyComplete();
    }

    @Test
    @Disabled
    void retrieveMovieInfo_RestClient() {
        var movieInfoFlux = movieInfoService.retrieveMovieInfo_RestClient(1).log();
        StepVerifier.create(movieInfoFlux).expectNextCount(1).verifyComplete();
        StepVerifier.create(movieInfoFlux).assertNext(movieInfo ->
                assertEquals("Batman Begins", movieInfo.getName()));
    }
}