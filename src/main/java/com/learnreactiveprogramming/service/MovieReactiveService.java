package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;


    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        final Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
        }).onErrorMap((ex) -> {
            log.error("Exception ::: " + ex);
            throw new MovieException(ex);
        });
    }

    public Flux<Movie> getAllMoviesRetry() {
        final Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
        }).onErrorMap((ex) -> {
            log.error("Exception ::: " + ex);
            throw new MovieException(ex);
        }).retry(3);
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        Retry retry = Retry.backoff(3, Duration.ofMillis(500))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
        final Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
        }).onErrorMap((ex) -> {
            log.error("Exception ::: " + ex);
            throw new MovieException(ex);
        }).retryWhen(retry);
    }

    public Flux<Movie> getAllMoviesRetryWhenWithFilter() {
        Retry retry = retryBackOffSpec();
        final Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
        }).onErrorMap((ex) -> {
            log.error("Exception ::: " + ex);
            if (ex instanceof NetworkException) {
                throw new MovieException(ex.getMessage());
            } else {
                throw new ServiceException(ex.getMessage());
            }
        }).retryWhen(retry);
    }

    public Flux<Movie> getAllMoviesRetryWhenRepeat(long n) {
        Retry retry = retryBackOffSpec();
        final Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();
        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                }).onErrorMap((ex) -> {
                    log.error("Exception ::: " + ex);
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    } else {
                        throw new ServiceException(ex.getMessage());
                    }
                })
                .retryWhen(retry)
                .repeat(n);
    }


    private Retry retryBackOffSpec() {
        return Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }

    public Mono<Movie> getMovie(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewMono = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewMono, (movieInfo, review) -> new Movie(movieInfo, review));
    }

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewMono = reviewService.retrieveReviewsFlux(movieId).collectList();
        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());
        return movieInfoMono
                .zipWith(reviewMono, (movieInfo, review) -> new Movie(movieInfo, review))
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                });
    }

    public Mono<Movie> getMovieWithFlatMap(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        return movieInfoMono.flatMap(movieInfo -> {
            var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
        });
    }
}
