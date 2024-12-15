package com.reactivespring.services;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.DuplicateMovieException;
import com.reactivespring.exception.MovieNotFoundException;
import com.reactivespring.exception.UpdateMovieException;
import com.reactivespring.repo.MovieInfoRepo;
import com.reactivespring.repo.MovieInfoRepoCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Slf4j
public class MoviesInfoService {

    @Autowired
    private MovieInfoRepo movieInfoRepo;

    @Autowired
    private MovieInfoRepoCustom movieInfoRepoCustom;

    public Mono<MovieInfo> getMovieById(String id) {
        return movieInfoRepo.findById(id).log();
    }

    public Mono<MovieInfo> saveMovieInfo(@RequestBody MovieInfo movieInfo) {
        log.info("Saving movie info: {}", movieInfo);
        return movieInfoRepo.save(movieInfo).log();
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepo.deleteById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo) {

        return movieInfoRepo.findById(id)
                .flatMap(existingMovie -> {
                    BeanUtils.copyProperties(movieInfo, existingMovie, "movieInfoId"); // Avoid copying the ID if it's
                    return movieInfoRepo.save(existingMovie);
                })
                .doOnSubscribe(subscription -> log.info("Attempting to update movie info for: {}", movieInfo.getName()))
                .doOnSuccess(
                        updatedMovie -> log.info("Successfully updated movie info for: {}", updatedMovie.getName()))
                .doOnError(throwable -> log.error("Error while updating movie info for {}: {}", movieInfo.getName(),
                        throwable.getMessage()))
                .onErrorResume(throwable -> {
                    if (throwable instanceof NoSuchElementException) {
                        log.warn("Movie not found: {}", movieInfo.getName());
                        return Mono.error(
                                new MovieNotFoundException("Movie not found: " + movieInfo.getName(), throwable));
                    }
                    if (throwable instanceof IncorrectResultSizeDataAccessException) {
                        log.warn("Movie not found: {}", movieInfo.getName());
                        return Mono.error(
                                new DuplicateMovieException("Movie not found: " + movieInfo.getName(), throwable));
                    } else {
                        log.error("Unexpected error while updating movie info", throwable);
                        return Mono.error(new UpdateMovieException("Error updating movie info", throwable));
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .log();
    }

    public Flux<MovieInfo> filterMovies(String name, Integer year, String cast, LocalDate releaseStartDate, LocalDate releaseEndDate) {

        if (Objects.isNull(releaseStartDate) && Objects.nonNull(releaseEndDate))
            releaseStartDate = releaseEndDate;
        else if (Objects.nonNull(releaseStartDate) && Objects.isNull(releaseEndDate))
            releaseEndDate = releaseStartDate;

        return movieInfoRepoCustom.filterMovies(name, year, cast, releaseStartDate, releaseEndDate);
    }
}
