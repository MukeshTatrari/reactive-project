package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    ReviewService reviewService = new ReviewService(webClient);

    @Test
    void retrieveReviews_RestClient() {

        var reviewFlux = reviewService.retrieveReviews_RestClient(1).log();
        StepVerifier.create(reviewFlux).expectNextCount(1).verifyComplete();
        StepVerifier.create(reviewFlux)
                .assertNext(review -> {
                    assertEquals("Nolan is the real superhero", review.getComment());
                }).verifyComplete();
    }
}