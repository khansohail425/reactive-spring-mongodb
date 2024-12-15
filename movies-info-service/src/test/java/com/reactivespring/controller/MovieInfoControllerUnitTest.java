package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.services.MoviesInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    static final String MOVIES_INFO_BASE_URL = "/api/v1/movieinfos";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MoviesInfoService moviesInfoService;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient.mutate().responseTimeout(Duration.ofMillis(30000000)).build();
    }

    @Test
    void testDeleteMovieInfo() {
        String movieId = "abc";
        String url = String.format("%s/%s", MOVIES_INFO_BASE_URL, movieId);
        when(moviesInfoService.deleteMovieInfo(eq(movieId))).thenReturn(Mono.empty());
        when(moviesInfoService.getMovieById(eq(movieId))).thenReturn(Mono.empty());
        webTestClient.delete().uri(url).exchange().expectStatus().isNoContent();
        webTestClient.get().uri(url).exchange().expectStatus().isNotFound();
    }

    @Test
    void testGetAllMovies() {
        var movieinfos = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        when(moviesInfoService.filterMovies(isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient.get().uri(MOVIES_INFO_BASE_URL).exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class).consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assertEquals(3, responseList.size());
                });

    }

    @Test
    void testGetMovieById() {

        when(moviesInfoService.getMovieById(eq("abc"))).thenReturn(Mono.just(
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20"))));

        String movieId = "/abc";
        String url = String.format("%s%s", MOVIES_INFO_BASE_URL, movieId);
        webTestClient.get().uri(url).exchange().expectStatus().is2xxSuccessful().expectBodyList(MovieInfo.class)
                .consumeWith(expectBodyList -> {
                    List<MovieInfo> responseList = expectBodyList.getResponseBody();
                    assertNotNull(responseList);
                    assert 1 == responseList.size();
                    assertEquals("Dark Knight Rises", responseList.getFirst().getName());
                });
    }

    @Test
    void testSaveMovieInfo() {
        MovieInfo requestBody = new MovieInfo("demoId", "Dark Knight Rises returns", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(moviesInfoService.saveMovieInfo(eq(requestBody))).thenReturn(Mono.just(requestBody));

        webTestClient.post().uri(MOVIES_INFO_BASE_URL).bodyValue(requestBody).exchange().expectStatus()
                .isCreated().expectBody().jsonPath("$.name").isEqualTo("Dark Knight Rises returns")
                .jsonPath("$.movieInfoId").isNotEmpty();

    }

    @Test
    void testUpdateMovieInfo() {
        String movieId = "abc";
        String url = String.format("%s/%s", MOVIES_INFO_BASE_URL, movieId);
        MovieInfo requestBody = new MovieInfo("abc", "Dark Knight Rises returns updated", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-25"));

        when(moviesInfoService.updateMovieInfo(isA(String.class), eq(requestBody)))
                .thenReturn(Mono.just(requestBody));

        webTestClient.put().uri(url).bodyValue(requestBody).exchange().expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.name").isEqualTo("Dark Knight Rises returns updated")
                .jsonPath("$.movieInfoId").isNotEmpty()
                .jsonPath("$.release_date", LocalDate.of(2012, 07, 25));

    }

    @Test
    void GivenBlankMovieName_WhenAddMovie_ThenThrowNoBlankValidationException() {
        MovieInfo requestBody = new MovieInfo("abc", "", -2012, List.of(""), LocalDate.parse("2012-07-25"));
        webTestClient.post()
                .uri(MOVIES_INFO_BASE_URL)
                .bodyValue(requestBody).exchange().expectStatus()
                .isBadRequest().expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message")
                .isArray()
                .jsonPath("$.message[0]").isEqualTo("movieInfo.cast must be present")
                .jsonPath("$.message[1]").isEqualTo("movieInfo.name must be present")
                .jsonPath("$.message[2]").isEqualTo("movieInfo.year must be a Positive Value");
    }

    @Test
    void WhenNonExistingMovie_ThenReturnNoFound() {
        when(moviesInfoService.getMovieById(isA(String.class))).thenReturn(Mono.empty());
        String movieId = "/abc";
        String url = String.format("%s%s", MOVIES_INFO_BASE_URL, movieId);
        webTestClient.get().uri(url)
                .exchange()
                .expectStatus().isNotFound()  // Expect 404 Not Found since the movie doesn't exist
                .expectBodyList(MovieInfo.class) // Expect empty body
                .consumeWith(response -> {
                    List<MovieInfo> responseList = response.getResponseBody();
                    assertNotNull(responseList); // Ensure the response body is not null
                    assertEquals(0, responseList.size()); // Ensure the response body is empty
                });
    }

    @Test
    void WhenNonExistingMovie_ThenReturnNotFound() {
        // Mock the service to return Mono.empty() for a non-existing movie
        when(moviesInfoService.getMovieById(any())).thenReturn(Mono.empty());

        // Prepare the movie ID and URL
        String movieId = "/abc";
        String url = String.format("%s%s", MOVIES_INFO_BASE_URL, movieId);

        // Perform the GET request and assert the response status and body

    }

}
