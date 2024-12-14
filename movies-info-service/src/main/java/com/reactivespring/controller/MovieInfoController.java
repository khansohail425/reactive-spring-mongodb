package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.services.MoviesInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("api/v1/movieinfos")
@Validated
public class MovieInfoController {

    private MoviesInfoService moviesInfoService;

    public MovieInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping
    public Flux<MovieInfo> getAllMovies() {
        return moviesInfoService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Mono<MovieInfo> getMovieById(@PathVariable(name = "id") String id) {
        return moviesInfoService.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<MovieInfo> saveMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.saveMovieInfo(movieInfo);
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
