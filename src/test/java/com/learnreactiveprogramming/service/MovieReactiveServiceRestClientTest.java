package com.learnreactiveprogramming.service;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Ignore
class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    private MovieInfoService movieInfoService = new MovieInfoService(webClient);
    private ReviewService reviewService = new ReviewService(webClient);
    private MovieReactiveService movieReactiveService =
            new MovieReactiveService(movieInfoService, reviewService);

    @Test
    void getAllMovies_RestClient() {
        var allMovies = movieReactiveService.getAllMovies_RestClient().log();
        StepVerifier.create(allMovies).expectNextCount(7).verifyComplete();
    }

    @Test
    void getMovies_RestClient() {
        var movieInfoFlux = movieReactiveService.getMovie_RestClient(1).log();
        StepVerifier.create(movieInfoFlux).expectNextCount(1).verifyComplete();
        StepVerifier.create(movieInfoFlux).assertNext(movieInfo ->
                assertEquals("Batman Begins", movieInfo.getMovie().getName()));
    }
}