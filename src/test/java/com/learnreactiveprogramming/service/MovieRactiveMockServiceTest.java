package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Test
    void getAllMoviesRetryException() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMsg));
        var moviesFlux = movieReactiveService.getAllMoviesRetry().log();
        StepVerifier.create(moviesFlux).expectError(MovieException.class).verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(anyLong());

    }

    @Test
    void getAllMoviesRetryWhenException() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMsg));
        var moviesFlux = movieReactiveService.getAllMoviesRetryWhen().log();
        StepVerifier.create(moviesFlux).expectError(MovieException.class).verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(anyLong());

    }

    @Test
    void getAllMoviesRetryWhenExceptionWithFilter() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new NetworkException(errorMsg));
        var moviesFlux = movieReactiveService.getAllMoviesRetryWhenWithFilter().log();
        StepVerifier.create(moviesFlux).expectError(MovieException.class).verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(anyLong());

    }

    @Test
    void getAllMoviesRetryWhenExceptionWithFilter_2() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new ServiceException(errorMsg));
        var moviesFlux = movieReactiveService.getAllMoviesRetryWhenWithFilter().log();
        StepVerifier.create(moviesFlux).expectError(ServiceException.class).verify();
        verify(reviewService, times(1)).retrieveReviewsFlux(anyLong());

    }

    @Test
    void getAllMoviesRetryWhenRepeat() {
        var errorMsg = "Exception occurred in review service";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();
        var moviesFlux = movieReactiveService.getAllMoviesRetryWhenRepeat(2).log();
        StepVerifier.create(moviesFlux).expectNextCount(6).thenCancel().verify();
        verify(reviewService, times(6)).retrieveReviewsFlux(anyLong());

    }
}
