package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieReactiveServiceTest {

    private MovieInfoService movieInfoService = new MovieInfoService();
    private ReviewService reviewService = new ReviewService();
    private MovieReactiveService movieReactiveService
            = new MovieReactiveService(movieInfoService, reviewService);

    @Test
    void getAllMovies() {
        var allMovies = movieReactiveService.getAllMovies().log();
        StepVerifier.create(allMovies).assertNext(movie -> {
            assertEquals("Batman Begins", movie.getMovie().getName());
            assertEquals(2, movie.getReviewList().size());
        }).assertNext(movie -> {
            assertEquals("The Dark Knight", movie.getMovie().getName());
            assertEquals(2, movie.getReviewList().size());
        }).assertNext(movie -> {
            assertEquals("Dark Knight Rises", movie.getMovie().getName());
            assertEquals(2, movie.getReviewList().size());
        }).verifyComplete();

    }

    @Test
    void testGetMovie() {
        var movieMono = movieReactiveService.getMovie(100L).log();
        StepVerifier.create(movieMono).assertNext(movie -> {
            assertEquals("Batman Begins", movie.getMovie().getName());
            assertEquals(2, movie.getReviewList().size());
        }).verifyComplete();
    }

    @Test
    void testGetMovieWithFlatMap() {
        var movieMono = movieReactiveService.getMovieWithFlatMap(100L).log();
        StepVerifier.create(movieMono).assertNext(movie -> {
            assertEquals("Batman Begins", movie.getMovie().getName());
            assertEquals(2, movie.getReviewList().size());
        }).verifyComplete();
    }
}