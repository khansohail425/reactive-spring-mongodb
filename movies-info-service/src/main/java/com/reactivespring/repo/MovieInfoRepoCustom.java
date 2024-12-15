package com.reactivespring.repo;

import com.reactivespring.domain.MovieInfo;
import reactor.core.publisher.Flux;

import java.time.LocalDate;


public interface MovieInfoRepoCustom {
    Flux<Long> getCountByYear(Integer year);

    Flux<MovieInfo> filterMovies(String name, Integer year, String cast, LocalDate releaseStartDate, LocalDate releaseEndDate);
}
