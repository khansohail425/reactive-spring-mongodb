package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.services.MoviesInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;

@RestController()
@RequestMapping("api/v1/movieinfos")
@Validated
public class MovieInfoController {

    private final MoviesInfoService moviesInfoService;

    public MovieInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<MovieInfo>>> getAllMovies(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "year") Integer year,
            @RequestParam(required = false, name = "cast") String cast,
            @RequestParam(required = false, name = "releaseStartDate") LocalDate releaseStartDate,
            @RequestParam(required = false, name = "releaseEndDate") LocalDate releaseEndDate) {
        Flux<MovieInfo> movies = moviesInfoService.filterMovies(name, year, cast, releaseStartDate, releaseEndDate);
        return Mono.just(ResponseEntity.ok(movies));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieById(@PathVariable(name = "id") String id) {
        return moviesInfoService.getMovieById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.defer(() -> Mono.just(ResponseEntity.notFound().build())));
    }

    @PostMapping
    public Mono<ResponseEntity<MovieInfo>> saveMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.saveMovieInfo(movieInfo)
                .map(ResponseEntity.created(URI.create(""))::body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable(name = "id") String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }

    @PutMapping("/{id}")
    public Mono<MovieInfo> updateMovieInfo(@PathVariable(name = "id") String id,
                                           @RequestBody(required = false) @Valid MovieInfo movieInfo) {
        return moviesInfoService.updateMovieInfo(id, movieInfo);
    }

}
