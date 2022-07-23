package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
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

    public Mono<Movie> getMovie(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewMono = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewMono, (movieInfo, review) -> new Movie(movieInfo, review));
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
