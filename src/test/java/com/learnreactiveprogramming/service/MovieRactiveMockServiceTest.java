package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.exception.MovieException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class MovieRactiveMockServiceTest {

    @Mock
    private MovieInfoService movieInfoService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private MovieReactiveService movieReactiveService;

    @Test
    void getAllMovies() {
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();
        var moviesFlux = movieReactiveService.getAllMovies().log();
        StepVerifier.create(moviesFlux).expectNextCount(3).verifyComplete();
    }

    @Test
    void getAllMoviesException() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMsg));
        var moviesFlux = movieReactiveService.getAllMovies().log();
        StepVerifier.create(moviesFlux).expectError(MovieException.class).verify();
    }
}
