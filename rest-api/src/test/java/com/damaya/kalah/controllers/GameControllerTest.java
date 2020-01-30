package com.damaya.kalah.controllers;

import com.damaya.kalah.KalahApiApplication;
import com.damaya.kalah.core.entities.domain.Game;
import com.damaya.kalah.core.entities.enums.GameTurn;
import com.damaya.kalah.core.interfaces.GameStorage;
import com.damaya.kalah.dtos.GameResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KalahApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    private static final String URL = "/games";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private GameStorage storage;

    @LocalServerPort
    private int port;

    @Test
    public void testCreateGameSucceed() {
        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        ResponseEntity<GameResponse> response = restTemplate.exchange(URL, HttpMethod.POST, entity, GameResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotNull().satisfies(game -> {
            assertThat(game.getId()).isNotEmpty();
            assertThat(game.getUrl()).isEqualTo("http://localhost:" + port + URL + "/" + game.getId());
            assertThat(game.getStatus()).isNull();
        });
    }

    @Test
    public void testMakeMoveFailedWhenGameNotFound() {
        String gameId = UUID.randomUUID().toString();
        int pitId = 6;
        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(URL + "/" + gameId + "/pits/" + pitId,
                HttpMethod.PUT, entity, responseType);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull().containsKeys("message").containsValue("Game not found");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testMakeMoveFailedWhenInvalidMoveAdversaryPit() {
        GameResponse game = createGameWithApi();
        int pitId = 13;

        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(URL + "/" + game.getId() + "/pits/" + pitId,
                HttpMethod.PUT, entity, responseType);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsKeys("message").containsValue("Player is trying to move stone from adversary pit");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testMakeMoveFailedWhenInvalidMoveStonesFromHomePit() {
        GameResponse game = createGameWithApi();
        int pitId = 7;

        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(URL + "/" + game.getId() + "/pits/" + pitId,
                HttpMethod.PUT, entity, responseType);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().containsKeys("message").containsValue("Stones from the house cannot be moved");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testMakeMoveFailedWhenGameIsAlreadyFinished() {
        Game game = createFinishedGame();
        int pitId = 2;
        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(URL + "/" + game.getId() + "/pits/" + pitId,
                HttpMethod.PUT, entity, responseType);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull().containsKeys("message").containsValue("Game is already finished");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    private GameResponse createGameWithApi(){
        HttpEntity<String> entity = createHttpEntity(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(URL, HttpMethod.POST, entity, GameResponse.class).getBody();
    }

    private Game createFinishedGame(){
        return storage.save(Game.builder()
                .id(UUID.randomUUID().toString())
                .startedAt(new Date())
                .finishedAt(new Date())
                .turn(GameTurn.PLAYER_ONE)
                .winner(GameTurn.PLAYER_ONE).build());
    }

    private HttpEntity<String> createHttpEntity(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new HttpEntity<>(headers);
    }
}