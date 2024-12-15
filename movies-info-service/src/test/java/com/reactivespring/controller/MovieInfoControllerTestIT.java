package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repo.MovieInfoRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerTestIT {

    static final String MOVIES_INFO_BASE_URL = "/api/v1/infos";
    @Autowired
    WebTestClient webClient;
    @Autowired
    MovieInfoRepo movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void testDeleteMovieInfo() {
        String movieId = "abc";
        String url = String.format("%s/%s", MOVIES_INFO_BASE_URL, movieId);
        webClient.delete().uri(url).exchange().expectStatus().isNoContent();
        webClient.get().uri(url).exchange().expectStatus().isNotFound();
    }

    @Test
    void testGetAllMovies() {
        webClient.get().uri(MOVIES_INFO_BASE_URL).exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assert 3 == responseList.size();
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


    @Test
    void givenYear_whenCallGetMovies_thenReturnMatchResult() {
        String request = UriComponentsBuilder.fromUriString(MOVIES_INFO_BASE_URL).queryParam("year", 2012).build().toUriString();
        webClient.get().uri(request).exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assertEquals(1, responseList.size());
                    assertEquals("Dark Knight Rises", responseList.getFirst().getName());
                });
    }


    @Test
    void givenNonExitingYear_whenCallGetMovies_thenReturnEmpty() {
        String request = UriComponentsBuilder.fromUriString(MOVIES_INFO_BASE_URL).queryParam("year", 2014).build().toUriString();
        webClient.get().uri(request).exchange().expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assertEquals(0, responseList.size());
                });
    }


}
