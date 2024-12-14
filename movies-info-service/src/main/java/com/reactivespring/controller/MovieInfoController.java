package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.services.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("api/v1/movieinfos")
public class MovieInfoController {

    @Autowired
    private MoviesInfoService moviesInfoService;

    @GetMapping
    public Flux<MovieInfo> getAllMovies() {
        return moviesInfoService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Mono<MovieInfo> getMovieById(@PathVariable(name = "id") String id) {
        return moviesInfoService.getMovieById(id);
    }

    @PostMapping
    public Mono<MovieInfo> saveMovieInfo(@RequestBody MovieInfo movieInfo) {
        return moviesInfoService.saveMovieInfo(movieInfo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable(name = "id") String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }

    @PutMapping("/{id}")
    public Mono<MovieInfo> updateMovieInfo(@RequestBody(required = false) MovieInfo movieInfo) {
        return moviesInfoService.updateMovieInfo(movieInfo);

    }

}
