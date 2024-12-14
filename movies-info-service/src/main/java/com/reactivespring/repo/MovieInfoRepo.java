package com.reactivespring.repo;


import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MovieInfoRepo extends ReactiveMongoRepository<MovieInfo, String> {
    Mono<MovieInfo> findByName(String name);
}
