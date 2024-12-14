package com.reactivespring;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repo.MovieInfoRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
class MoviesServiceApplicationTests {

    @Autowired
    MovieInfoRepo movieInfoRepo;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        movieInfoRepo.saveAll(List.of(
                        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                                LocalDate.parse("2005-06-15")),
                        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
                                LocalDate.parse("2008-07-18")),
                        new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                                LocalDate.parse("2012-07-20"))))
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepo.deleteAll().block();
    }

    @Test
    void getAllMovies() {
        // when
        Flux<MovieInfo> all = movieInfoRepo.findAll().log();

        // then
        StepVerifier.create(all).expectNextCount(3).verifyComplete();

    }

    @Test
    void getMovieByName() {
        Mono<MovieInfo> byId = movieInfoRepo.findByName("Batman Begins").log();
        StepVerifier.create(byId).assertNext(movieInfo -> {
            assert movieInfo.getName().equals("Batman Begins");
            assert movieInfo.getYear() == 2005;
        }).verifyComplete();
    }

    @Test
    void getMovieById() {
        Mono<MovieInfo> byId = movieInfoRepo.findById("abc").log();
        StepVerifier.create(byId).assertNext(movieInfo -> {
            assert movieInfo.getName().equals("Dark Knight Rises");
        }).verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises 01", 2012, List.of("Christian Bale", "Tom Hardy"),
                LocalDate.parse("2012-07-20"));
        Mono<MovieInfo> savedMovieInfo = movieInfoRepo.save(movieInfo).log();
        StepVerifier.create(savedMovieInfo).assertNext(movie -> {
            assert movie.getMovieInfoId() != null;
        }).verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises", 2010, List.of("Christian Bale", "Tom Hardy"),
                LocalDate.parse("2012-07-20"));

        // Save the movieInfo in a non-blocking way
        Mono<MovieInfo> savedMovieInfoMono = movieInfoRepo.save(movieInfo).doOnNext(movie -> {
            // Log the saved movie info
            System.out.println("Saved Movie: " + movie);
        });

        // Use StepVerifier to test the non-blocking behavior
        StepVerifier.create(savedMovieInfoMono).assertNext(movie -> {
            assert movie.getMovieInfoId() != null;
            assert movie.getName().equals("Dark Knight Rises");
            assert movie.getYear() == 2010;
        }).verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        movieInfoRepo.deleteById("abc").block();
        Flux<MovieInfo> log = movieInfoRepo.findAll().log();
        StepVerifier.create(log).expectNextCount(2).verifyComplete();
    }
}
