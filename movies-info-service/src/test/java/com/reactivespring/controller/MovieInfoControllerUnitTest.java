package com.reactivespring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.services.MoviesInfoService;

import reactor.core.publisher.Flux;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    static final String MOVIES_INFO_BASE_URL = "/api/v1/movieinfos";

    @Autowired
    WebTestClient webClient;

    @MockBean
    MoviesInfoService moviesInfoService;

    @Test
    void testDeleteMovieInfo() {
        String movieId = "abc";
        String url = String.format("%s/%s", MOVIES_INFO_BASE_URL, movieId);
        webClient.delete().uri(url).exchange().expectStatus().isNoContent();
        webClient.get().uri(url).exchange().expectStatus().is2xxSuccessful().expectBody().isEmpty();
    }

    @Test
    void testGetAllMovies() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(moviesInfoService.getAllMovies()).thenReturn(Flux.fromIterable(movieinfos));

        webClient.get().uri(MOVIES_INFO_BASE_URL).exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assertEquals(3, responseList.size());
                });
    }

    @Test
    void testGetMovieById() {
        String movieId = "/abc";
        String url = String.format("%s%s", MOVIES_INFO_BASE_URL, movieId);
        webClient.get().uri(url).exchange().expectStatus().is2xxSuccessful().expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assert 1 == responseList.size();
                    assertEquals("Dark Knight Rises", responseList.getFirst().getName());
                });
    }

    @Test
    void testSaveMovieInfo() {
        MovieInfo requestBody = new MovieInfo(null, "Dark Knight Rises returns",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        webClient.post().uri(MOVIES_INFO_BASE_URL).bodyValue(requestBody).exchange().expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises returns").jsonPath("$.movieInfoId").isNotEmpty();

    }

    @Test
    void testUpdateMovieInfo() {
        String movieId = "abc";
        String url = String.format("%s/%s", MOVIES_INFO_BASE_URL, movieId);
        MovieInfo requestBody = new MovieInfo("abc", "Dark Knight Rises returns updated",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-25"));

        webClient.put().uri(url).bodyValue(requestBody).exchange().expectStatus().is2xxSuccessful().expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises returns updated").jsonPath("$.movieInfoId")
                .isNotEmpty().jsonPath("$.release_date", LocalDate.of(2012, 07, 25));

    }
}
